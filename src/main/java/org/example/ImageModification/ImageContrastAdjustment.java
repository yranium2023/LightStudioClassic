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

public class ImageContrastAdjustment extends ImageAdjustment {
    private static double lastValue = 0.0;
    private static double contrastValue;
    private static ChangeListener<Number> contrastSliderListener;

    /**
     * @Description  该方法用于绑定对比度调整滑动条
     * @param contrastSlider
     * @param editingImageObj
     * @author 吴鹄远
     * @date 2023/12/12 16:27
    **/

    public static void contrastAdjustBind(VSlider contrastSlider, ImageObj editingImageObj){
        if(editingImageObj!=null){
            System.out.println("bind success");
            // 移除之前的监听器
            if (contrastSliderListener != null) {
                contrastSlider.percentageProperty().removeListener(contrastSliderListener);
            }
            contrastSlider.setPercentage(StaticValues.editingImageObj.getContrastPercent());
            double threshold = 0.1; // 定义阈值，每次滑动长度大于该值时认为值发生改变
            lastValue=1.0;
            // 创建新的监听器
            contrastSliderListener = (obs, old, now) -> {
                if (old == now) return;
                if(editingImageObj.getNowSlider_1()!= ImageObj.sliderType_1.CONTRAST){
                    bufferedImage = ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
                    processedImage = new BufferedImage(
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);
                    editingImageObj.setNowSlider_1(ImageObj.sliderType_1.CONTRAST);
                }
                double newValue = 0.2 + contrastSlider.getPercentage() * 1.6;
                if (Math.abs(newValue - lastValue) > threshold) {
                    contrastValue = 2 - newValue;
                    adjustContrastAsync(editingImageObj);
                    lastValue = newValue;
                }
            };
            // 添加新的监听器
            contrastSlider.percentageProperty().addListener(contrastSliderListener);
            contrastSlider.setOnMouseReleased(e->{
                editingImageObj.setContrastPercent(contrastSlider.getPercentage());
                editingImageObj.addHistory(new AdjustHistory("对比度调整",contrastValue));
            });
        }
    }

    public static void setContrastValue(double contrastValue) {
        ImageContrastAdjustment.contrastValue = contrastValue;
    }

    public static void adjustContrastAsync(ImageObj editingImageObj) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            new ThreadProcess(bufferedImage, processedImage) {
                @Override
                public int calculateRGB(int rgb) {
                    int alpha = (rgb >> 24) & 0xFF;
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // Adjust the contrast
                    red = (int) (Math.pow(red / 255.0, 1.0 / contrastValue) * 255.0);
                    green = (int) (Math.pow(green / 255.0, 1.0 / contrastValue) * 255.0);
                    blue = (int) (Math.pow(blue / 255.0, 1.0 / contrastValue) * 255.0);
                    // Clamp the values to the valid range [0, 255]
                    red = Math.max(0, Math.min(255, red));
                    green = Math.max(0, Math.min(255, green));
                    blue = Math.max(0, Math.min(255, blue));
                    // Compose the adjusted color
                    return (alpha << 24) | (red << 16) | (green << 8) | blue;
                }
            }.run();
            javafx.application.Platform.runLater(() -> {
                Image adjustedImage = SwingFXUtils.toFXImage(processedImage, null);
                //设置新图像
//                editingImageObj.getEditImages().add(adjustedImage);
                editingImageObj.renewAll(adjustedImage);
                //刷新显示的图像
                ImageEditScene.initEditImagePane();
                System.out.println("对比度调整成功");
            });
        });
        executor.shutdown();
    }


}
