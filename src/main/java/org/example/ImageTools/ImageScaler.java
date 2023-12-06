package org.example.ImageTools;

/**
 * @author 申雄全
 * @Description:该类用于编辑图片时使得图片进行合理的缩放或者扩大显示
 * @date 2023/12/5 16:01
 */

import io.vproxy.vfx.util.FXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


public class ImageScaler {

    public static ImageView getImageView(Image image, Pane pane) {

        // 创建ImageView来显示图片
        ImageView imageView = new ImageView(image);
        // 设置ImageView的属性，以实现缩放
        imageView.setPreserveRatio(true); // 保持宽高比
        imageView.setFitWidth(pane.getPrefWidth()-20);        // 设置宽度，根据需要调整
        imageView.setFitHeight(imageView.getImage().getHeight()*imageView.getFitWidth()/imageView.getImage().getWidth());
        imageView.setX((pane.getPrefWidth()-imageView.getFitWidth())/2);
        imageView.setY((pane.getPrefHeight()-imageView.getFitHeight())/2);
        return imageView;
    }
}
