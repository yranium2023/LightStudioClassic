package org.example.ImageTools;

/**
 * @author 申雄全
 * @Description:该类用于编辑图片时使得图片进行合理的缩放或者扩大显示
 * @date 2023/12/5 16:01
 */

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class ImageScaler {

    public static ImageView getImageView(Image image) {

        // 创建ImageView来显示图片
        ImageView imageView = new ImageView(image);
        // 设置ImageView的属性，以实现缩放
        imageView.setPreserveRatio(true); // 保持宽高比
        imageView.setFitWidth(900);        // 设置宽度，根据需要调整
        imageView.setFitHeight(imageView.getImage().getHeight()*imageView.getFitWidth()/imageView.getImage().getWidth());
        return imageView;
    }
}
