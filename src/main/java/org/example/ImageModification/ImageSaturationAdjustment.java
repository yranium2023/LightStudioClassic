package org.example.ImageModification;

/**
 * @author 申雄全
 * @Description
 * @date 2023/12/9 15:00
 */

import io.vproxy.vfx.ui.slider.VSlider;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.example.ImageTools.ImageTransfer;
import org.example.Obj.AdjustHistory;
import org.example.Obj.ImageObj;
import org.example.Scene.ImageEditScene;
import org.example.StaticValues;

import java.awt.image.BufferedImage;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageSaturationAdjustment extends ImageAdjustment {

    private static double lastValue=1;
    private static double saturationValue;
    private static ChangeListener<Number> SliderListener;


    /**
     * @Description  该方法用于绑定饱和度调整滑动条
     * @param saturationSlider
     * @param editingImageObj
     * @author 吴鹄远
     * @date 2023/12/12 16:44
    **/

    public static void saturationAdjustBind(VSlider saturationSlider, ImageObj editingImageObj){
        if(editingImageObj!=null){
            System.out.println("bind success");
            // 移除之前的监听器
            if (SliderListener != null) {
                saturationSlider.percentageProperty().removeListener(SliderListener);
            }
            saturationSlider.setPercentage(StaticValues.editingImageObj.getSaturationPercent());
            double threshold = 0.1; // 定义阈值，每次滑动长度大于该值时认为值发生改变
            lastValue=1;
            // 创建新的监听器
            SliderListener = (obs, old, now) -> {
                if (old == now) return;
                if(editingImageObj.getNowSlider_1()!= ImageObj.sliderType_1.SATURATION){
                    bufferedImage = ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
                    ImageAdjustment.setProcessedImage();
                    editingImageObj.setNowSlider_1(ImageObj.sliderType_1.SATURATION);
                }
                double newValue = 0 + saturationSlider.getPercentage() * 2;//0 1 2
                if (Math.abs(newValue - lastValue) > threshold) {
                    saturationValue = newValue;
                    ExecutorService executor = Executors.newCachedThreadPool();
                    executor.submit(() -> {
                        adjustSaturationAsync();
                        javafx.application.Platform.runLater(() -> {
                            Image adjustedImage = SwingFXUtils.toFXImage(processedImage, null);
                            //设置新图像
                            editingImageObj.renewAll(adjustedImage);
                            //刷新显示的图像
                            ImageEditScene.initEditImagePane();
                            System.out.println("饱和度调整成功");
                        });
                    });
                    executor.shutdown();
                    lastValue = newValue;
                }
            };
            // 添加新的监听器
            saturationSlider.percentageProperty().addListener(SliderListener);
            saturationSlider.setOnMouseReleased(e->{
                editingImageObj.setSaturationPercent(saturationSlider.getPercentage());
                editingImageObj.addHistory(new AdjustHistory("饱和度调整",saturationValue));
            });
        }
    }

    public static void adjustSaturationAsync() {

            new ThreadProcess(bufferedImage,processedImage){
                @Override
                public int calculateRGB(int rgb) {
                    int alpha = (rgb >> 24) & 0xFF;
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    double[] hsl = rgbToHsl(red, green, blue);
                    hsl[1] *= saturationValue;
                    hsl[1] = Math.max(0, Math.min(1, hsl[1]));
                    return hslToRgb(hsl[0], hsl[1], hsl[2], alpha);
                }
            }.run();

    }

    public static void setSaturationValue(double saturationValue) {
        ImageSaturationAdjustment.saturationValue = saturationValue;
    }

    // 将 RGB 转换为 HSL
    private static double[] rgbToHsl(int r, int g, int b) {
        double[] hsl = new double[3];
        double var_R = (r / 255.0);
        double var_G = (g / 255.0);
        double var_B = (b / 255.0);

        double var_Min = Math.min(var_R, Math.min(var_G, var_B));
        double var_Max = Math.max(var_R, Math.max(var_G, var_B));
        double del_Max = var_Max - var_Min;

        double H, S, L;
        L = (var_Max + var_Min) / 2.0;

        if (del_Max - 0.01 <= 0.0) {
            H = 0;
            S = 0;
        } else {
            if (L < 0.5) {
                S = del_Max / (var_Max + var_Min);
            } else {
                S = del_Max / (2 - var_Max - var_Min);
            }
            double del_R = (((var_Max - var_R) / 6.0) + (del_Max / 2.0)) / del_Max;
            double del_G = (((var_Max - var_G) / 6.0) + (del_Max / 2.0)) / del_Max;
            double del_B = (((var_Max - var_B) / 6.0) + (del_Max / 2.0)) / del_Max;
            if (var_R == var_Max) {
                H = del_B - del_G;
            } else if (var_G == var_Max) {
                H = (1.0 / 3.0) + del_R - del_B;
            } else {
                H = (2.0 / 3.0) + del_G - del_R;
            }
            if (H < 0) {
                H += 1;
            }
            if (H > 1) {
                H -= 1;
            }
        }
        hsl[0] = H;
        hsl[1] = S;
        hsl[2] = L;
        return hsl;
    }

    // 将 HSL 转换回 RGB
    private static int hslToRgb(double h, double s, double l,int alpha) {
        int r, g, b;

        if (s <= 0.01) {
            r = (int) (l * 255.0);
            g = (int) (l * 255.0);
            b = (int) (l * 255.0);
        } else {
            double var_2;
            if (l < 0.5) {
                var_2 = l * (1 + s);
            } else {
                var_2 = (l + s) - (s * l);
            }

            double var_1 = 2 * l - var_2;

            r = (int) (255.0 * hueToRgb(var_1, var_2, h + (1.0 / 3.0)));
            g = (int) (255.0 * hueToRgb(var_1, var_2, h));
            b = (int) (255.0 * hueToRgb(var_1, var_2, h - (1.0 / 3.0)));
        }

        return  (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    private static double hueToRgb(double v1, double v2, double vH) {
        if (vH < 0.0) {
            vH += 1.0;
        }
        if (vH > 1.0) {
            vH -= 1.0;
        }
        if ((6.0 * vH) < 1.0) {
            return (v1 + (v2 - v1) * 6.0 * vH);
        }
        if ((2.0 * vH) < 1.0) {
            return (v2);
        }
        if ((3.0 * vH) < 2.0) {
            return (v1 + (v2 - v1) * ((2.0 / 3.0) - vH) * 6.0);
        }
        return (v1);
    }


}






