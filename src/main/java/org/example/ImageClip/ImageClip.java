package org.example.ImageClip;


import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import org.example.ImageStatistics.Histogram;
import org.example.Pane.ImagePane;
import org.example.ImageTools.ImageScaler;
import org.example.Obj.ImageObj;
import org.example.Scene.ImageClipScene;
import org.example.StaticValues;


/**
 * @author 申雄全，吴鹄远
 * @Description 此类实现行图片裁剪
 * @date 2023/12/3 20:22
 */


public class ImageClip {
    private static Rectangle clip = new Rectangle(50, 50, 100, 100);
    private static double mouseX;
    private static double mouseY;
    private static Shape darkenedArea;

    private static Rectangle imageViewRect;
    /**
     * @Description 此方法实现图片的裁剪
     * @param editingImageObj
     * @param imagePane
     * @author 申雄全
     * @updateTime 2023/12/23 22:54
     */
    public static void imageClip(ImageObj editingImageObj, ImagePane imagePane) {

        Image image=editingImageObj.getEditingImage();


        // 创建ImageView并设置图像
        ImageView imageView = ImageScaler.getImageView(image, imagePane);

        clip.setStrokeWidth(3);
        clip.setStrokeType(StrokeType.CENTERED);
        clip.setStroke(Color.rgb(189,200,191));
        clip.setFill(Color.TRANSPARENT);

        imageViewRect = new Rectangle(imageView.getX(), imageView.getY(), imageView.getFitWidth(), imageView.getFitHeight());
        bindAll(imageViewRect,imageView);
        clip.setX(imageView.getX());
        clip.setY(imageView.getY());
        clip.setWidth(3000);
        clip.setHeight(3000);

        darkenedArea = Shape.subtract(imageViewRect, clip);
        darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
        imagePane.getChildren().addAll(imageView, darkenedArea, clip);
        imageView.fitWidthProperty().addListener((ob,old,now)->{
            if(old!=now){
                enSureClipInRec();
                int index = imagePane.getChildren().indexOf(darkenedArea);
                darkenedArea = Shape.subtract(imageViewRect, new Rectangle(
                        clip.getX(),
                        clip.getY(),
                        clip.getWidth(),
                        clip.getHeight()
                ));
                darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
                imagePane.getChildren().set(index, darkenedArea);
            }
        });
        imageView.fitHeightProperty().addListener((ob,old,now)->{
            if(old!=now){
                enSureClipInRec();
                int index = imagePane.getChildren().indexOf(darkenedArea);
                darkenedArea = Shape.subtract(imageViewRect, new Rectangle(
                        clip.getX(),
                        clip.getY(),
                        clip.getWidth(),
                        clip.getHeight()
                ));
                darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
                imagePane.getChildren().set(index, darkenedArea);
            }
        });

        imagePane.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            clip.setOnMouseExited(e -> {
                imagePane.setCursor(Cursor.DEFAULT);
            });

            if (mouseX <= clip.getX() + clip.getWidth() + 5 && mouseX >= clip.getX() + clip.getWidth() - 5 && mouseY <= clip.getY() + clip.getHeight() + 5 && mouseY >= clip.getY() + clip.getHeight() - 5) {
                clip.setCursor(Cursor.NW_RESIZE);
                clipMove(imageView, imagePane, 0, 0, 1, 1);
            } else if (mouseX >= clip.getX() - 10 && mouseX <= clip.getX() + 10 && mouseY >= clip.getY() - 10 && mouseY <= clip.getY() + 10) {
                //改变鼠标样式
                clip.setCursor(Cursor.NW_RESIZE);
                //监视鼠标移动控制截图区移动
                clipMove(imageView, imagePane, 1, 1, -1, -1);
            } else if (mouseY <= clip.getY() + 5 && mouseY >= clip.getY() && mouseX <= clip.getX() + clip.getWidth() + 5 && mouseX >= clip.getX() + clip.getWidth() - 5) {
                //右上
                clip.setCursor(Cursor.NE_RESIZE);
                clipMove(imageView, imagePane, 0, 1, 1, -1);
            } else if (mouseX >= clip.getX() - 10 && mouseX <= clip.getX() + 10 && mouseY <= clip.getY() + clip.getHeight() + 5 && mouseY >= clip.getY() + clip.getHeight() - 5) {
                //左下
                clip.setCursor(Cursor.NE_RESIZE);
                clipMove(imageView, imagePane, 1, 0, -1, 1);
            } else if (Math.abs(mouseX - clip.getX() - clip.getWidth() / 2) < 3 && Math.abs(mouseY - clip.getY()) < 5) {
                //上
                clip.setCursor(Cursor.N_RESIZE);
                clipMove(imageView, imagePane, 0, 1, 0, -1);
            } else if (Math.abs(mouseX - clip.getX() - clip.getWidth() / 2) < 3 && Math.abs(mouseY - clip.getY() - clip.getHeight()) < 5) {
                //下
                clip.setCursor(Cursor.S_RESIZE);
                clipMove(imageView, imagePane, 0, 0, 0, 1);
            } else if (Math.abs(mouseX - clip.getX()) < 3 && Math.abs(mouseY - clip.getY() - clip.getHeight() / 2) < 5) {
                //左
                clip.setCursor(Cursor.W_RESIZE);
                clipMove(imageView, imagePane, 1, 0, -1, 0);
            } else if (Math.abs(mouseX - clip.getX() - clip.getWidth()) < 3 && Math.abs(mouseY - clip.getY() - clip.getHeight() / 2) < 5) {
                //右
                clip.setCursor(Cursor.E_RESIZE);
                clipMove(imageView, imagePane, 0, 0, 1, 0);
            } else {
                clip.setCursor(Cursor.MOVE);
                clipMove(imageView, imagePane, 1, 1, 0, 0);
            }
        });
        //确认裁剪
        ImageClipScene.getAffirmButton().setOnAction(event->{
            System.out.println("确认裁剪成功");
            ImageClipScene.getCancelButton().setDisable(false);
            ImageClipScene.getResetButton().setDisable(false);
            Image imageToClip;
            if(editingImageObj.getClipImages().isEmpty()){
                imageToClip=editingImageObj.getOriginalImage();
            }else{
                int lastIndex=editingImageObj.getClipImages().size()-1;
                imageToClip=editingImageObj.getClipImages().get(lastIndex);
            }
            //获取当前原图和当前显示的图像的比例
            double ratio=imageToClip.getWidth()/imageView.getFitWidth();
            //获取裁剪的起点
            double clipX=clip.getX()-imageView.getX();
            double clipY=clip.getY()-imageView.getY();
            //首先获得裁剪的图像
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT); // 设置背景为透明
            //保存裁剪图片
            //在裁剪时，所有的参数乘上系数
            params.setViewport(new Rectangle2D(clipX*ratio, clipY*ratio, clip.getWidth()*ratio,clip.getHeight()*ratio)); // 设置裁剪区域
            // 调用snapshot方法获取裁剪后的图像
            ImageView ImageViewToClip=new ImageView(imageToClip);
            WritableImage snapshot = ImageViewToClip.snapshot(params, null);
            // 将 WritableImage 转换为 Image
            Image clippedImage = SwingFXUtils.toFXImage(SwingFXUtils.fromFXImage(snapshot, null), null);
            //把生成的Image加入clipImage的List中
            editingImageObj.getClipImages().add(clippedImage);
            editingImageObj.renewAll(clippedImage);
            editingImageObj.editingImageToHistory();
            //将当前界面上的图片进行替换
            int index=imagePane.getChildren().indexOf(imageView);
            copyImageViewProperties(ImageScaler.getImageView(editingImageObj.getEditingImage(),imagePane),imageView,imagePane);
            imagePane.getChildren().set(index,imageView);
            clip.setX(imageView.getX());
            clip.setY(imageView.getY());
            clip.setWidth(3000);
            clip.setHeight(3000);
            enSureClipInRec();
            initImageViewRect(imageViewRect,imageView);
            int indexOfDark = imagePane.getChildren().indexOf(darkenedArea);
            darkenedArea = Shape.subtract(imageViewRect, new Rectangle(
                    clip.getX(),
                    clip.getY(),
                    clip.getWidth(),
                    clip.getHeight()
            ));
            darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
            imagePane.getChildren().set(indexOfDark, darkenedArea);
            //更新直方图
            Histogram.drawHistogram(StaticValues.editingImageObj.getEditingImage());
        });

        //取消裁剪，回到裁剪前的图像
        ImageClipScene.getCancelButton().setOnAction(event -> {
            System.out.println("取消裁剪成功");
            //获取上一张图片
            Image preImage;
            if(editingImageObj.getClipImages().size()>1){
                int size=editingImageObj.getClipImages().size();
                preImage=editingImageObj.getClipImages().get(size-2);
            }else{
                preImage=editingImageObj.getOriginalImage();
            }
            //删除上一次裁剪得到的图片
            int lastIndex=editingImageObj.getClipImages().size()-1;
            editingImageObj.getClipImages().remove(lastIndex);
            //假如没有裁剪图片了，就设置disable
            if(editingImageObj.getClipImages().isEmpty()){
                ImageClipScene.getCancelButton().setDisable(true);
                ImageClipScene.getResetButton().setDisable(true);
            }
            //生成和替换缩略图
            Image newButtonImage=ImageObj.resizeButtonImage(preImage);
            editingImageObj.renewAll(preImage);
            editingImageObj.editingImageToHistory();
            //将当前界面上的图片进行替换
            int index=imagePane.getChildren().indexOf(imageView);
            copyImageViewProperties(ImageScaler.getImageView(editingImageObj.getEditingImage(), imagePane),imageView,imagePane);
            imagePane.getChildren().set(index,imageView);
            clip.setX(imageView.getX());
            clip.setY(imageView.getY());
            clip.setWidth(3000);
            clip.setHeight(3000);
            enSureClipInRec();
            initImageViewRect(imageViewRect,imageView);
            int indexOfDark = imagePane.getChildren().indexOf(darkenedArea);
            darkenedArea = Shape.subtract(imageViewRect, new Rectangle(
                    clip.getX(),
                    clip.getY(),
                    clip.getWidth(),
                    clip.getHeight()
            ));
            darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
            imagePane.getChildren().set(indexOfDark, darkenedArea);
            Histogram.drawHistogram(StaticValues.editingImageObj.getEditingImage());
        });

        //复位按钮
        ImageClipScene.getResetButton().setOnAction(event -> {
            System.out.println("复位操作成功");
            //获取原始图片
            Image originImage=editingImageObj.getOriginalImage();
            //清空裁剪List
            if(!editingImageObj.getClipImages().isEmpty()){
                editingImageObj.getClipImages().clear();
            }
            //设置复位和取消图标为不可用
            ImageClipScene.getResetButton().setDisable(true);
            ImageClipScene.getCancelButton().setDisable(true);
            editingImageObj.renewAll(originImage);
            editingImageObj.editingImageToHistory();
            //将当前界面上的图片进行替换
            int index=imagePane.getChildren().indexOf(imageView);
            copyImageViewProperties(ImageScaler.getImageView(editingImageObj.getEditingImage(),imagePane),imageView,imagePane);
            imagePane.getChildren().set(index,imageView);
            clip.setX(imageView.getX());
            clip.setY(imageView.getY());
            clip.setWidth(3000);
            clip.setHeight(3000);
            enSureClipInRec();
            initImageViewRect(imageViewRect,imageView);
            int indexOfDark = imagePane.getChildren().indexOf(darkenedArea);
            darkenedArea = Shape.subtract(imageViewRect, new Rectangle(
                    clip.getX(),
                    clip.getY(),
                    clip.getWidth(),
                    clip.getHeight()
            ));
            darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
            imagePane.getChildren().set(indexOfDark, darkenedArea);
            Histogram.drawHistogram(StaticValues.editingImageObj.getEditingImage());
        });

    }


    private static void clipMove(ImageView imageView, Pane anchorPane, int x, int y, int width, int height) {
        anchorPane.setOnMouseDragged(mouseEvent -> {
            double deltaX = mouseEvent.getX() - mouseX;
            double deltaY = mouseEvent.getY() - mouseY;
            double clipX = clip.getX() + x * deltaX, clipY = clip.getY() + y * deltaY;
            double clipWidth = clip.getWidth() + width * deltaX, clipHeight = clip.getHeight() + height * deltaY;
            if (clipX > imageView.getX() && clipX < imageViewRect.getX() + imageViewRect.getWidth() - clipWidth) {
                clip.setX(clipX);
                clip.setWidth(clipWidth);
            }
            if (clipY > imageView.getY() && clipY < imageViewRect.getY() + imageViewRect.getHeight() - clipHeight) {
                clip.setY(clipY);
                clip.setHeight(clipHeight);
            }


            mouseX = mouseEvent.getX();
            mouseY = mouseEvent.getY();
            //图片外阴影区域设置，待优化
            int index = anchorPane.getChildren().indexOf(darkenedArea);
            darkenedArea = Shape.subtract(imageViewRect, new Rectangle(
                    clip.getX(),
                    clip.getY(),
                    clip.getWidth(),
                    clip.getHeight()
            ));
            darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
            anchorPane.getChildren().set(index, darkenedArea);
        });
    }
    /**
     * @Description 实现在Rec的变化中clip一直都不超出rec的区域
     * @author 吴鹄远
     * @date 2023/12/9 14:03
    **/

    public static void enSureClipInRec(){
        Bounds clipBounds=clip.getBoundsInLocal();
        Bounds imageViewBounds=imageViewRect.getBoundsInLocal();
        if(!imageViewBounds.contains(clipBounds)){
            double newClipX = clip.getX();
            double newClipY = clip.getY();

            // 调整 X 坐标
            if (clipBounds.getMinX() < imageViewBounds.getMinX()) {
                newClipX += imageViewBounds.getMinX() - clipBounds.getMinX();
            } else if (clipBounds.getMaxX() > imageViewBounds.getMaxX()) {
                newClipX -= clipBounds.getMaxX() - imageViewBounds.getMaxX();
            }

            // 调整 Y 坐标
            if (clipBounds.getMinY() < imageViewBounds.getMinY()) {
                newClipY += imageViewBounds.getMinY() - clipBounds.getMinY();
            } else if (clipBounds.getMaxY() > imageViewBounds.getMaxY()) {
                newClipY -= clipBounds.getMaxY() - imageViewBounds.getMaxY();
            }

            //调整大小
            if(clipBounds.getHeight()>imageViewBounds.getHeight()){
                clip.setHeight(imageViewBounds.getHeight()-5);
            }
            if(clipBounds.getWidth()>imageViewBounds.getWidth()){
                clip.setWidth(imageViewBounds.getWidth()-5);
            }

            // 设置新的 clip 位置
            clip.setX(newClipX);
            clip.setY(newClipY);
        }

    }
    /**
     * @Description 复制ImageView属性的方法 
     * @param sourceImageView
     * @param targetImageView
     * @author 吴鹄远
     * @date 2023/12/9 22:38
    **/
    
    private static void copyImageViewProperties(ImageView sourceImageView, ImageView targetImageView,ImagePane imagePane) {
        double paneRatio=imagePane.getWidth()/imagePane.getHeight();
        // 设置目标ImageView的属性
        double ratio=sourceImageView.getFitWidth()/sourceImageView.getFitHeight();
        targetImageView.setImage(sourceImageView.getImage());
        targetImageView.fitWidthProperty().unbind();
        targetImageView.fitHeightProperty().unbind();
        targetImageView.setFitWidth(sourceImageView.getFitWidth());
        targetImageView.setFitHeight(sourceImageView.getFitHeight());
        ImageScaler.initImageView(targetImageView,imagePane,ratio);
        if(ratio>paneRatio){
            targetImageView.fitWidthProperty().bind(imagePane.widthProperty().multiply(0.95));
            targetImageView.fitHeightProperty().bind(targetImageView.fitWidthProperty().multiply(1/ratio));
        }else{
            targetImageView.fitHeightProperty().bind(imagePane.heightProperty().multiply(0.95));
            targetImageView.fitWidthProperty().bind(targetImageView.fitHeightProperty().multiply(ratio));
        }
    }
    /**
     * @Description 绑定rect的所有属性
     * @param imageViewRect
     * @param imageView
     * @author 吴鹄远
     * @date 2023/12/9 23:33
    **/

    private static void bindAll(Rectangle imageViewRect,ImageView imageView){
        imageViewRect.widthProperty().bind(imageView.fitWidthProperty());
        imageViewRect.heightProperty().bind(imageView.fitHeightProperty());
        imageViewRect.xProperty().bind(imageView.xProperty());
        imageViewRect.yProperty().bind(imageView.yProperty());
    }
    /**
     * @Description 解绑rect的所有属性
     * @param imageViewRect
     * @param imageView
     * @author 吴鹄远
     * @date 2023/12/9 23:34
    **/

    private static void unBindAll(Rectangle imageViewRect,ImageView imageView){
        imageViewRect.widthProperty().unbind();
        imageViewRect.heightProperty().unbind();
        imageViewRect.xProperty().unbind();
        imageViewRect.yProperty().unbind();
    }
    /**
     * @Description  重新生成遮罩
     * @param imageViewRect
     * @param imageView
     * @author 吴鹄远
     * @date 2023/12/9 23:34
    **/

    private static void initImageViewRect(Rectangle imageViewRect,ImageView imageView){
        unBindAll(imageViewRect,imageView);
        imageViewRect.setX(imageView.getX());
        imageViewRect.setY(imageView.getY());
        imageViewRect.setWidth(imageView.getFitWidth());
        imageViewRect.setHeight(imageViewRect.getHeight());
        bindAll(imageViewRect,imageView);
    }




}




