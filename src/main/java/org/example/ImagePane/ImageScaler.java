package org.example.ImagePane;

/**
 * @author 申雄全
 * @Description:该类用于编辑图片时使得图片进行合理的缩放或者扩大显示
 * @date 2023/12/5 16:01
 */

import io.vproxy.vfx.util.FXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.example.ImagePane.ImagePane;


public class ImageScaler {

    public static ImageView getImageView(Image image, ImagePane imagePane) {

        // 创建ImageView来显示图片
        ImageView imageView = new ImageView(image);
        // 设置ImageView的属性，以实现缩放
        imageView.setPreserveRatio(true); // 保持宽高比
        //获得图片的宽高比
        double x=image.getWidth();
        double y=image.getHeight();
        double ratio=x/y;
        //初始状态下，设置宽高
        if(ratio>1){
            imageView.setFitWidth(imagePane.getWidth()*0.95);        // 设置宽度，根据需要调整
            imageView.setFitHeight(imageView.getFitWidth()/ratio);
        }else{
            imageView.setFitHeight(imagePane.getHeight()*0.95);
            imageView.setFitWidth(imageView.getFitHeight()*ratio);
        }
        System.out.println(imagePane.getWidth());
        System.out.println(imageView.getFitWidth());
        imageView.setX((imagePane.getWidth()-imageView.getFitWidth())/2);
        imageView.setY((imagePane.getHeight()-imageView.getFitHeight())/2);
        if(ratio>1){
            imageView.fitWidthProperty().bind(imagePane.widthProperty().multiply(0.95));
            imageView.fitHeightProperty().bind(imageView.fitWidthProperty().multiply(1/ratio));
        }else{
            imageView.fitHeightProperty().bind(imagePane.heightProperty().multiply(0.95));
            imageView.fitWidthProperty().bind(imageView.fitHeightProperty().multiply(ratio));
        }
        imagePane.widthProperty().addListener((ob,old,now)->{
            double v= now.doubleValue();
            System.out.println(v);
            System.out.println(imageView.fitWidthProperty().doubleValue());
            imageView.setX((v-imageView.fitWidthProperty().doubleValue())/2);
        });
        imagePane.heightProperty().addListener((ob,old,now)->{
            double v= now.doubleValue();
            imageView.setY((v-imageView.fitHeightProperty().doubleValue())/2);
        });
        return imageView;
    }
}
