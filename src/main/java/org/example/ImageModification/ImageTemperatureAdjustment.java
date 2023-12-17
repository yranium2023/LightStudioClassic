package org.example.ImageModification;


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


/**
 * @author 申雄全
 * @Description 该类实现色温调整
 * @date 2023/12/8 22:38
 */
public class ImageTemperatureAdjustment extends ImageAdjustment {
    private static double lastValue;
    private static double kelvin;//色温
    private static double originalTemperature;//初始色温
    private static double redStrength, greenStrength, blueStrength;
    private static ChangeListener<Number> SliderListener;

    public static void temperatureAdjustBind(VSlider temperatureSlider, ImageObj editingImageObj){
        if(editingImageObj!=null){
            System.out.println("bind success");
           /* ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                originalTemperature = calculateColorTemperature();
            });*/
            // 移除之前的监听器
            if (SliderListener != null) {
                temperatureSlider.percentageProperty().removeListener(SliderListener);
            }
            temperatureSlider.setPercentage(StaticValues.editingImageObj.getTemperaturePercent());
            double threshold = 0.04; // 定义阈值，每次滑动长度大于该值时认为值发生改变
            lastValue=1;
            // 创建新的监听器
            SliderListener = (obs, old, now) -> {
                if (old == now) return;
                if(editingImageObj.getNowSlider_1()!= ImageObj.sliderType_1.TEMPERATURE){
                    bufferedImage = ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
                    ImageAdjustment.setProcessedImage();
                    editingImageObj.setNowSlider_1(ImageObj.sliderType_1.TEMPERATURE);
                }
                double newValue = temperatureSlider.getPercentage() * 1.95;//0.15 1 1.85
                if (Math.abs(newValue - lastValue) > threshold) {
                    kelvin =6500*(2-newValue);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(() -> {
                        adjustTemperatureAsync();
                        javafx.application.Platform.runLater(() -> {
                            Image adjustedImage = SwingFXUtils.toFXImage(processedImage, null);
                            //设置新图像
                            //editingImageObj.getEditImages().add(adjustedImage);
                            editingImageObj.renewAll(adjustedImage);
                            //刷新显示的图像
                            ImageEditScene.initEditImagePane();
                            System.out.println("色温调整成功");
                        });
                    });
                    executor.shutdown();
                    lastValue = newValue;
                }
            };
            // 添加新的监听器
            temperatureSlider.percentageProperty().addListener(SliderListener);
            temperatureSlider.setOnMouseReleased(e->{
                editingImageObj.setTemperaturePercent(temperatureSlider.getPercentage());
                editingImageObj.addHistory(new AdjustHistory("色温调整",kelvin));
            });
        }
    }
    public static void adjustTemperatureAsync() {
            TemperatureToRGB();
            new ThreadProcess(bufferedImage, processedImage) {
                @Override
                public int calculateRGB(int rgb) {
                    // Extract individual color components
                    int alpha = (rgb >> 24) & 0xFF;
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    red = (int) (red * (redStrength / 255));
                    green = (int) (green * (greenStrength / 255));
                    blue = (int) (blue * (blueStrength / 255));
                    // Compose the adjusted color
                    return (alpha << 24) | (red << 16) | (green << 8) | blue;
                }
            }.run();
    }

    public static void setKelvin(double kelvin) {
        ImageTemperatureAdjustment.kelvin = kelvin;
    }

    private static double calculateColorTemperature( ){

        // 计算图像的红色、绿色和蓝色通道的平均值
        double totalRed = 0.0, totalGreen = 0.0, totalBlue = 0.0;
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int totalPixels = width * height;
        // 遍历图像的每个像素
        double maxRed=0,maxGreen=0,maxBlue=0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = bufferedImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                // 累加每个通道的值
                if(red>maxRed) maxRed=red;
                if(green>maxGreen) maxGreen=green;
                if(blue>maxBlue) maxBlue=blue;
                totalRed += red;
                totalGreen += green;
                totalBlue += blue;
            }
        }
        double averageRed = totalRed / totalPixels;
        double averageGreen = totalGreen / totalPixels;
        double averageBlue = totalBlue / totalPixels;
        double x = (-0.14282) * averageRed + (1.54924) * averageGreen + (-0.95641) *averageBlue;
        double y = (-0.32466) * averageRed + (1.57837) * averageGreen + (-0.73191) * averageBlue;
        double n = (x - 0.3320) / (y - 0.1858);

        return (449 * Math.pow(n, 3)) + (3525 * Math.pow(n, 2)) + (6823.3 * n) + 5520.33;

    }
    private static void TemperatureToRGB(){
        double Temperature = kelvin / 100;

        if (Temperature < 66) {
            redStrength = 255;
        } else {
            redStrength = Temperature - 60;
            redStrength = 329.698727446 * Math.pow(redStrength, -0.1332047592);
            redStrength = Math.max(0, Math.min(redStrength, 255));
        }
        if (Temperature < 66) {
            greenStrength = Temperature;
            greenStrength = 99.4708025861 * Math.log(greenStrength) - 161.1195681661;
            greenStrength = Math.max(0, Math.min(greenStrength, 255));
        } else {
            greenStrength = Temperature - 60;
            greenStrength = 288.1221695283 * Math.pow(greenStrength, -0.0755148492);
            greenStrength = Math.max(0, Math.min(greenStrength, 255));
        }
        if (Temperature >= 66) {
            blueStrength = 255;
        } else {
            if (Temperature <= 19) {
                blueStrength= 0;
            } else {
                blueStrength = Temperature - 10;
                blueStrength = 138.5177312231 * Math.log(blueStrength) - 305.0447927307;
                blueStrength = Math.max(0, Math.min(blueStrength, 255));
            }
        }


    }
}

