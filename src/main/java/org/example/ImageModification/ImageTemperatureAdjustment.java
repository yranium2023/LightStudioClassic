package org.example.ImageModification;


import io.vproxy.vfx.ui.slider.VSlider;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ImageTools.ImageTransfer;
import org.example.Obj.ImageObj;
import org.example.Scene.ImageEditScene;
import org.example.StaticValues;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author 申雄全
 * @Description 该类实现色温调整
 * @date 2023/12/8 22:38
 */
public class ImageTemperatureAdjustment extends ImageAdjustment {
    private static double lastValue;
    private static double Temperature;//色温
    private static double originalTemperature;//初始色温
    private static double redStrength, greenStrength, blueStrength;
    private static ChangeListener<Number> SliderListener;

    public static void temperatureAdjustBind(VSlider temperatureSlider, ImageObj editingImageObj){
        if(editingImageObj!=null){
            System.out.println("bind success");
            bufferedImage = ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
            processedImage = new BufferedImage(
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                originalTemperature = calculateColorTemperature();
            });
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
                double newValue = 0.15 + temperatureSlider.getPercentage() * 1.7;//0.15 1 1.85
                if (Math.abs(newValue - lastValue) > threshold) {
                    Temperature =originalTemperature*(2-newValue);
                    adjustTemperatureAsync(editingImageObj);
                    lastValue = newValue;
                }
            };
            // 添加新的监听器
            temperatureSlider.percentageProperty().addListener(SliderListener);
            temperatureSlider.setOnMouseReleased(e->{
                editingImageObj.setTemperaturePercent(temperatureSlider.getPercentage());
            });
        }
    }
    private static void adjustTemperatureAsync(ImageObj editingImageObj) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            calculateRGBStrength();
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
            javafx.application.Platform.runLater(() -> {
                Image adjustedImage = SwingFXUtils.toFXImage(processedImage, null);
                //设置新图像
                editingImageObj.getEditImages().add(adjustedImage);
                editingImageObj.renewAll(adjustedImage);
                //刷新显示的图像
                ImageEditScene.initEditImagePane();
                System.out.println("色温调整成功");
            });
        });
    }
    private static double calculateColorTemperature() {
        int totalPixels = bufferedImage.getWidth() * bufferedImage.getHeight();
        int width= bufferedImage.getWidth(),height= bufferedImage.getHeight();
        int redTotal = 0;
        int greenTotal = 0;
        int blueTotal = 0;
        for (int x = 0; x < width; x++) {
            for (int y= 0; y < height; y++) {
                int rgb = bufferedImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                redTotal += red;
                greenTotal += green;
                blueTotal += blue;
            }
        }
        double r = (double) redTotal /totalPixels / 255.0;
        double g = (double) greenTotal / totalPixels / 255.0;
        double b = (double) blueTotal / totalPixels / 255.0;
        double x = (-0.14282) * r + (1.54924) * g + (-0.95641) * b;
        double y = (-0.32466) * r + (1.57837) * g + (-0.73191) * b;
        // 根据Y值计算相关色温
        double n = (x - 0.3320) / (y - 0.1858);
        return (449 * Math.pow(n, 3)) + (3525 * Math.pow(n, 2)) + (6823.3 * n) + 5520.33; // 返回色温值（单位：Kelvin）
    }


    private static void calculateRGBStrength(){
        double kelvin = Temperature / 100;
        if (kelvin < 66) {
            redStrength = 255;
        } else {
            redStrength = kelvin - 60;
            redStrength = 329.698727446 * Math.pow(redStrength, -0.1332047592);
            redStrength = Math.max(0, Math.min(redStrength, 255));
        }
        if (kelvin < 66) {
            greenStrength = kelvin;
            greenStrength = 99.4708025861 * Math.log(greenStrength) - 161.1195681661;
            greenStrength = Math.max(0, Math.min(greenStrength, 255));
        } else {
            greenStrength = kelvin - 60;
            greenStrength = 288.1221695283 * Math.pow(greenStrength, -0.0755148492);
            greenStrength = Math.max(0, Math.min(greenStrength, 255));
        }
        if (kelvin >= 66) {
            blueStrength = 255;
        } else {
            if (kelvin <= 19) {
                blueStrength = 0;
            } else {
                blueStrength = kelvin - 10;
                blueStrength = 138.5177312231 * Math.log(blueStrength) - 305.0447927307;
                blueStrength = Math.max(0, Math.min(blueStrength, 255));
            }
        }
    }
}

