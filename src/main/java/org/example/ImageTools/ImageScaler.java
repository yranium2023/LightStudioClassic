package org.example.ImageTools;



import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.Pane.ImagePane;
/**
 * 该类用于编辑图片时使得图片进行合理的缩放或者扩大显示
 * @author 申雄全
 * Date 2023/12/5 16:01
 */

public class ImageScaler {

    public static ImageView getImageView(Image image, ImagePane imagePane) {

        // 创建ImageView来显示图片
        ImageView imageView = new ImageView(image);
        // 设置ImageView的属性，以实现缩放
        imageView.setPreserveRatio(true); // 保持宽高比
        //需要获取imagePane的宽高比
        double paneRatio=imagePane.getWidth()/imagePane.getHeight();
        //获得图片的宽高比
        double x=image.getWidth();
        double y=image.getHeight();
        double ratio=x/y;
        initImageView(imageView,imagePane,ratio);
        if(ratio>paneRatio){
            imageView.fitWidthProperty().bind(imagePane.widthProperty().multiply(0.95));
            imageView.fitHeightProperty().bind(imageView.fitWidthProperty().multiply(1/ratio));
        }else{
            imageView.fitHeightProperty().bind(imagePane.heightProperty().multiply(0.95));
            imageView.fitWidthProperty().bind(imageView.fitHeightProperty().multiply(ratio));
        }
        imagePane.widthProperty().addListener((ob,old,now)->{
            if(imageView.getFitWidth()!=0){
                double v= now.doubleValue();
                imageView.setX((v-imageView.getFitWidth())/2);
                imageView.fitWidthProperty().unbind();
                imageView.fitHeightProperty().unbind();
                double paneRatio1=imagePane.getWidth()/imagePane.getHeight();
                double ratio1=imageView.getFitWidth()/imageView.getFitHeight();
                if(ratio1>paneRatio1){
                    imageView.fitWidthProperty().bind(imagePane.widthProperty().multiply(0.95));
                    imageView.fitHeightProperty().bind(imageView.fitWidthProperty().multiply(1/ratio1));
                }else{
                    imageView.fitHeightProperty().bind(imagePane.heightProperty().multiply(0.95));
                    imageView.fitWidthProperty().bind(imageView.fitHeightProperty().multiply(ratio1));
                }
            }
        });
        imagePane.heightProperty().addListener((ob,old,now)->{
            if(imageView.getFitHeight()!=0){
                double v= now.doubleValue();
                imageView.setY((v-imageView.getFitHeight())/2);
                imageView.fitWidthProperty().unbind();
                imageView.fitHeightProperty().unbind();
                double paneRatio1=imagePane.getWidth()/imagePane.getHeight();
                double ratio1=imageView.getFitWidth()/imageView.getFitHeight();
                if(ratio1>paneRatio1){
                    imageView.fitWidthProperty().bind(imagePane.widthProperty().multiply(0.95));
                    imageView.fitHeightProperty().bind(imageView.fitWidthProperty().multiply(1/ratio1));
                }else{
                    imageView.fitHeightProperty().bind(imagePane.heightProperty().multiply(0.95));
                    imageView.fitWidthProperty().bind(imageView.fitHeightProperty().multiply(ratio1));
                }
            }
        });
        return imageView;
    }
    public static void initImageView(ImageView imageView, ImagePane imagePane, double ratio) {
        double paneRatio=imagePane.getWidth()/imagePane.getHeight();
        // 初始状态下，设置宽高
        if (ratio > paneRatio) {
            imageView.setFitWidth(imagePane.getWidth() * 0.95);
            imageView.setFitHeight(imageView.getFitWidth() / ratio);
        } else {
            imageView.setFitHeight(imagePane.getHeight() * 0.95);
            imageView.setFitWidth(imageView.getFitHeight() * ratio);
        }

        // 计算初始位置使图片居中
        double initialX = (imagePane.getWidth() - imageView.getFitWidth()) / 2;
        double initialY = (imagePane.getHeight() - imageView.getFitHeight()) / 2;
        imageView.setX(initialX);
        imageView.setY(initialY);
    }


}
