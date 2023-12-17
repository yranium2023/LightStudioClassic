package org.example.HSL;

import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.ImageModification.ImageAdjustment;
import org.example.ImageModification.ThreadProcess;
import org.example.ImageTools.ImageTransfer;
import org.example.Obj.AdjustHistory;
import org.example.Obj.HSLColor;
import org.example.Obj.HSLInfo;
import org.example.Obj.ImageObj;
import org.example.Scene.ImageEditScene;

import java.awt.image.BufferedImage;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 申雄全
 * @Description 该类实现HSL调整，对每个颜色调整色相，饱和度，亮度
 * @date 2023/12/9 13:48
 */
interface MyFunction {
    boolean doSomething(int rgb);

}
public class HSLColorAdjustment extends ImageAdjustment {

    private static final double [] lastValue={0,0,0};

    private static int selectedColor=0;

    private static int selectedProperty;

    private static double  huePer=0, satuPer=0,lumPer=0;
    private static ChangeListener<Number> hueListener;
    private static ChangeListener<Number> saturationListener;
    private static ChangeListener<Number> luminanceListener;
    private static final MyFunction[] isColor=new MyFunction[7];
    static{
        isColor[0]=HSLColorAdjustment::isRed;
        isColor[1]=HSLColorAdjustment::isOrange;
        isColor[2]=HSLColorAdjustment::isYellow;
        isColor[3]=HSLColorAdjustment::isGreen;
        isColor[4]=HSLColorAdjustment::isCyan;
        isColor[5]=HSLColorAdjustment::isBlue;
        isColor[6]=HSLColorAdjustment::isPurple;
    }
    public static void hslButtonBind(HSLColor hslColor, ImageObj editingImageObj){
        System.out.println("绑定"+hslColor+"成功");
        if(editingImageObj!=null){
            var hueSlider_HSL=ImageEditScene.hueSlider_HSL;
            var saturationSlider_HSL=ImageEditScene.saturationSlider_HSL;
            var luminanceSlider_HSL=ImageEditScene.luminanceSlider_HSL;
            bufferedImage = ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
            processedImage = new BufferedImage(
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );
            //移除之前的监视器
            if(hueListener!=null){
                hueSlider_HSL.percentageProperty().removeListener(hueListener);
            }
            if(saturationListener!=null){
                saturationSlider_HSL.percentageProperty().removeListener(saturationListener);
            }
            if(luminanceListener!=null){
                luminanceSlider_HSL.percentageProperty().removeListener(luminanceListener);
            }
            selectedColor=hslColor.ordinal();
            HSLInfo hslInfo=editingImageObj.getHslInfos().get(hslColor);
            hueSlider_HSL.setPercentage(hslInfo.getHuePercent());
            saturationSlider_HSL.setPercentage(hslInfo.getSaturationPercent());
            luminanceSlider_HSL.setPercentage(hslInfo.getLuminancePercent());
            //判断间隔
            double threshold =0.03;
            lastValue[0]=-0.18+hueSlider_HSL.getPercentage()*0.36;;
            lastValue[1]=-0.3+hueSlider_HSL.getPercentage()*0.6;
            lastValue[2]=-0.2+0.4*luminanceSlider_HSL.getPercentage();
            //色相轴
            hueListener=(ob,old,now)->{
                if (old == now) return;
                System.out.println("正在调整hue");
                if(hslInfo.getNowType()!= HSLInfo.sliderType.HUE){
                    bufferedImage = ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
                    processedImage = new BufferedImage(
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight(),
                            BufferedImage.TYPE_INT_ARGB
                    );
                    hslInfo.setNowType(HSLInfo.sliderType.HUE);
                }
                huePer=-0.18+hueSlider_HSL.getPercentage()*0.36;
                selectedProperty=0;
                if(Math.abs(huePer-lastValue[0])>threshold){
                    HSLAdjust(editingImageObj);
                    lastValue[0]=huePer;
                }
            };
            hueSlider_HSL.percentageProperty().addListener(hueListener);
            hueSlider_HSL.setOnMouseReleased(e->{
                hslInfo.setHuePercent(hueSlider_HSL.getPercentage());
            });

            //饱和度轴
            saturationListener=(ob,old,now)->{
                System.out.println("正在调整饱和度");
                if(old==now)return;
                if(hslInfo.getNowType()!= HSLInfo.sliderType.SATURATION){
                    bufferedImage = ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
                    processedImage = new BufferedImage(
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight(),
                            BufferedImage.TYPE_INT_ARGB
                    );
                    hslInfo.setNowType(HSLInfo.sliderType.SATURATION);
                }
                satuPer=-0.3+saturationSlider_HSL.getPercentage()*0.6;
                selectedProperty=1;
                if(Math.abs(satuPer-lastValue[1])>threshold){
                    HSLAdjust(editingImageObj);
                    lastValue[1]=satuPer;
                }
            };
            saturationSlider_HSL.percentageProperty().addListener(saturationListener);
            saturationSlider_HSL.setOnMouseReleased(e->{
                hslInfo.setSaturationPercent(saturationSlider_HSL.getPercentage());
            });

            //明度轴
            luminanceListener=(ob,old,now)->{
                if(old==now)return;
                if(hslInfo.getNowType()!= HSLInfo.sliderType.LUMINANCE){
                    bufferedImage = ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
                    processedImage = new BufferedImage(
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight(),
                            BufferedImage.TYPE_INT_ARGB
                    );
                    hslInfo.setNowType(HSLInfo.sliderType.LUMINANCE);
                }
                lumPer=-0.2+0.4*luminanceSlider_HSL.getPercentage();
                selectedProperty=2;
                if(Math.abs(lumPer-lastValue[2])>threshold){
                    HSLAdjust(editingImageObj);
                    lastValue[2]=lumPer;
                }
            };
            luminanceSlider_HSL.percentageProperty().addListener(luminanceListener);
            luminanceSlider_HSL.setOnMouseReleased(e->{
                hslInfo.setLuminancePercent(luminanceSlider_HSL.getPercentage());
                switch (selectedProperty){
                    case 0:
                        AdjustHistory.addHistory(new AdjustHistory("HSL色相调整",LocalTime.now().truncatedTo(ChronoUnit.SECONDS),selectedColor,selectedProperty));
                        break;
                    case 1:
                        AdjustHistory.addHistory(new AdjustHistory("HSL饱和度调整",LocalTime.now().truncatedTo(ChronoUnit.SECONDS),selectedColor,selectedProperty));
                        break;
                    case 2:
                        AdjustHistory.addHistory(new AdjustHistory("HSL明度调整",LocalTime.now().truncatedTo(ChronoUnit.SECONDS),selectedColor,selectedProperty));
                        break;
                }
            });

        }
    }


    private static void HSLAdjust(ImageObj editingImageObj){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(()->{
            new ThreadProcess(bufferedImage,processedImage){
                @Override
                public int calculateRGB(int rgb) {
                    if(isColor[selectedColor].doSomething(rgb)){
                        int alpha = (rgb >> 24) & 0xFF;
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                       double []hsl=rgbToHsl(r,g,b);
                       switch (selectedProperty){
                           case 0:
                               hsl[0] += huePer;
                               if (hsl[0] > 1.0) {
                                   hsl[0] -= 1.0; // 如果超出了1，将其回归到0-1的范围内
                               } else if (hsl[0] < 0.0) {
                                   hsl[0] += 1;
                               }
                               break;
                           case 1:
                               hsl[1] +=satuPer;
                               hsl[1]=Math.max(0.0,Math.min(1.0,hsl[1]));

                               break;
                           default:
                               hsl[2] +=lumPer;
                               hsl[2]=Math.max(0.0,Math.min(1.0,hsl[2]));
                               break;
                       }

                        return hslToRgb(hsl[0],hsl[1],hsl[2],alpha);
                    }else{
                        return rgb;
                    }
                }
            }.run();
            javafx.application.Platform.runLater(() -> {
                Image adjustedImage = ImageTransfer.toJavaFXImage(processedImage);
                //设置新图像
                editingImageObj.renewAll(adjustedImage);
                //刷新显示的图像
                ImageEditScene.initEditImagePane();
                System.out.println("调整完毕");
            });
        });
        executor.shutdown();
    }

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

        if (del_Max == 0.0) {
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

        if (s ==0.0) {
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

            double var_1 = 2.0 * l - var_2;

            r = (int) (255.0 * hueToRgb(var_1, var_2, h + (1.0 / 3.0)));
            g = (int) (255.0 * hueToRgb(var_1, var_2, h));
            b = (int) (255.0 * hueToRgb(var_1, var_2, h - (1.0 / 3.0)));
        }

        return  (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    private static double hueToRgb(double v1, double v2, double vH) {
        if (vH < 0.0) vH += 1.0;
        if (vH > 1.0) vH -= 1.0;
        if ((6.0 * vH) < 1.0) return (v1 + (v2 - v1) * 6.0 * vH);
        if ((2.0 * vH) < 1.0) return (v2);
        if ((3.0 * vH) < 2.0) return (v1 + (v2 - v1) * ((2.0 / 3.0) - vH) * 6.0);
        return (v1);
    }

    // 判断红色
    private static boolean isRed(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red > 155 && green < 160 && blue < 160; // 提高红色识别条件
    }


    private static boolean isOrange(int rgb){
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red > 155 && green > 55 && blue < 95;
    }

    private static boolean isYellow(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red > 155 && green > 125 && blue < 120;
    }

    private static boolean isGreen(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red < 115 && green > 105 && blue < 175; // 缩小绿色判断范围
    }

    private static boolean isBlue(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red < 125 && green < 175 && blue > 105; // 降低蓝色识别条件
    }
    private static boolean isCyan(int rgb){
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return green > 115 && blue > 115 && red < 155; // 扩大青色判断范围
    }

    private static boolean isPurple(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red > 105 && green < 195 && blue > 105;
    }
}
