package org.example.ImageTools;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;

/**
 * @author 申雄全
 * @Description 该类工具类，实现image和bufferedImage的相互转换
 * @date 2023/12/5 20:37
 */
public class ImageTransfer {
    public static BufferedImage toBufferedImage(Image image) {
        return SwingFXUtils.fromFXImage(image, null);
    }

    public static Image toJavaFXImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }
}
