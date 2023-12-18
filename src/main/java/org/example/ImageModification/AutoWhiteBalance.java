package org.example.ImageModification;

/**
 * @author 申雄全
 * @Description 该类实现自动白平衡
 * @date 2023/12/9 14:56
 */
import io.vproxy.vfx.ui.button.ImageButton;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ImageTools.ImageTransfer;
import org.example.Obj.ImageObj;
import org.example.Scene.ImageEditScene;

import java.awt.image.BufferedImage;
import java.util.concurrent.*;


public class AutoWhiteBalance extends ImageAdjustment{

    private static double  redGain,greenGain,blueGain;

    /**
     * @Description  该方法用于实现按钮对图片进行自动白平衡调整
     * @param editingImageObj
     * @author 吴鹄远
     * @date 2023/12/11 15:26
    **/

    public static void autoWhiteBalance(ImageObj editingImageObj){
        bufferedImage=ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
        processedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            WhiteBalance();
            javafx.application.Platform.runLater(() -> {
                Image adjustedImage = SwingFXUtils.toFXImage(processedImage, null);
                //设置新图像
                editingImageObj.renewAll(adjustedImage);
                //刷新显示的图像
                ImageEditScene.initEditImagePane();
            });
        });
        executor.shutdown();
    }

    private static void calculateRGBGain(){
        // 计算图像的红色、绿色和蓝色通道的平均值
        double totalRed = 0.0, totalGreen = 0.0, totalBlue = 0.0;
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int totalPixels = width * height;
        // 遍历图像的每个像素
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = bufferedImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                // 累加每个通道的值
                totalRed += red;
                totalGreen += green;
                totalBlue += blue;
            }
        }
        // 计算平均值
        double averageRed = totalRed / totalPixels;
        double averageGreen = totalGreen / totalPixels;
        double averageBlue = totalBlue / totalPixels;
        // 计算平均灰度值
        double grayValue = (averageRed + averageGreen + averageBlue) / 3.0;
        // 计算红色、绿色和蓝色通道的增益
        redGain = grayValue / averageRed;
        greenGain = grayValue / averageGreen;
        blueGain = grayValue / averageBlue;
    }

    public static void WhiteBalance(){
           calculateRGBGain();
            new ThreadProcess(bufferedImage, processedImage) {
                @Override
                public int calculateRGB(int rgb) {
                    int alpha = (rgb >> 24) & 0xFF;
                    int red = (int) (((rgb >> 16) & 0xFF) * redGain);
                    int green = (int) (((rgb >> 8) & 0xFF) * greenGain);
                    int blue = (int) ((rgb & 0xFF) * blueGain);
                    // Clamp the values to the valid range [0, 255]
                    red = Math.min(255, Math.max(0, red));
                    green = Math.min(255, Math.max(0, green));
                    blue = Math.min(255, Math.max(0, blue));
                    // 更新调整后的颜色值
                    return  (alpha << 24)|(red << 16) | (green << 8) | blue;
                }
            }.run();
    }

}

