package org.example.ImageModification;

import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;

/**
 *  该类为图片调整的父类，存一些常用的属性和方法
 * @author 申雄全
 * @author 吴鹄远
 * Date 2023/12/11 22:35
 */
public class ImageAdjustment {

    public static BufferedImage bufferedImage;
    public static BufferedImage processedImage;

    public static void setProcessedImage() {
     processedImage =new BufferedImage(
             bufferedImage.getWidth(),
             bufferedImage.getHeight(),
             BufferedImage.TYPE_INT_ARGB
     );
    }
    public static void setBufferedImage(){
        bufferedImage=new BufferedImage(
                processedImage.getWidth(),
                processedImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
    }

}
