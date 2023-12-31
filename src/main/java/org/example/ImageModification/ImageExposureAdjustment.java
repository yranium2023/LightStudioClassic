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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 该类实现图片的曝光度调整
 *
 * @author 申雄全, 吴鹄远
 * Date 2023/12/22 22:38
 */
public class ImageExposureAdjustment extends ImageAdjustment {


    private static double lastValue = 0.0;
    private static double exposureValue;
    private static ChangeListener<Number> SliderListener;

    /**
     * 该方法用于绑定曝光度调整滑动条
     *
     * @param exposureSlider
     * @param editingImageObj
     * @author 吴鹄远
     * Date 2023/12/12 16:27
     **/

    public static void exposerAdjustBind(VSlider exposureSlider, ImageObj editingImageObj) {
        if (editingImageObj != null) {
            System.out.println("bind success");
            // 移除之前的监听器
            if (SliderListener != null) {
                exposureSlider.percentageProperty().removeListener(SliderListener);
            }
            exposureSlider.setPercentage(StaticValues.editingImageObj.getExposurePercent());
            double threshold = 0.1; // 定义阈值，每次滑动长度大于该值时认为值发生改变
            lastValue = 0;
            // 创建新的监听器
            SliderListener = (obs, old, now) -> {
                if (old == now) return;
                if (editingImageObj.getNowSlider_1() != ImageObj.sliderType_1.EXPOSURE) {
                    bufferedImage = ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
                    ImageAdjustment.setProcessedImage();
                    editingImageObj.setNowSlider_1(ImageObj.sliderType_1.EXPOSURE);
                }
                System.out.println(editingImageObj.getNowSlider_1());
                double newValue = -1 + exposureSlider.getPercentage() * 2;//-1 0 1
                if (Math.abs(newValue - lastValue) > threshold) {
                    exposureValue = newValue;
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(() -> {
                        adjustExposureAsync();
                        javafx.application.Platform.runLater(() -> {
                            Image adjustedImage = SwingFXUtils.toFXImage(processedImage, null);
                            //设置新图像
//                editingImageObj.getEditImages().add(adjustedImage);
                            editingImageObj.renewAll(adjustedImage);
                            //刷新显示的图像
                            ImageEditScene.initEditImagePane();
                            System.out.println("曝光度调整成功");

                        });
                    });
                    executor.shutdown();
                    lastValue = newValue;
                }
            };
            // 添加新的监听器
            exposureSlider.percentageProperty().addListener(SliderListener);
            exposureSlider.setOnMouseReleased(e -> {
                editingImageObj.setExposurePercent(exposureSlider.getPercentage());
                editingImageObj.addHistory(new AdjustHistory("曝光度调整", exposureValue));
            });
        }
    }

    /**
     * 该方法实现曝光度调整算法
     *
     * @author 申雄全
     * Date 2023/12/23 23:24
     */
    public static void adjustExposureAsync() {

        new ThreadProcess(bufferedImage, processedImage) {
            @Override
            public int calculateRGB(int rgb) {
                int alpha = (rgb >> 24) & 0xFF;
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                // Adjust the brightness (exposure)
                red = (int) (red * (1 + exposureValue));
                green = (int) (green * (1 + exposureValue));
                blue = (int) (blue * (1 + exposureValue));
                // Clamp the values to the valid range [0, 255]
                red = Math.max(0, Math.min(255, red));
                green = Math.max(0, Math.min(255, green));
                blue = Math.max(0, Math.min(255, blue));
                // Compose the adjusted color
                return (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
        }.run();
    }

    public static void setExposureValue(double exposureValue) {
        ImageExposureAdjustment.exposureValue = exposureValue;
    }
}
