package org.example.ImageModification;


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
import org.example.ImagePane.ImagePane;
import org.example.ImagePane.ImageScaler;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


/**
 * @author misssing
 * @Description: 此类实现行图片裁剪
 * @date 2023/12/3 20:22
 */


public class ImageClip {
    private static Rectangle clip = new Rectangle(50, 50, 100, 100);
    private static double mouseX;
    private static double mouseY;
    private static Shape darkenedArea;

    private static Rectangle imageViewRect;

    /**
     * @describle 此方法实现图片的裁剪
     * @author missing
     * @updateTime 2023/12/4 10:59
     */
    public static void imageClip(Image image, ImagePane anchorPane) {

        // 创建ImageView并设置图像
        ImageView imageView = ImageScaler.getImageView(image, anchorPane);

        clip.setStrokeWidth(3);
        clip.setStrokeType(StrokeType.CENTERED);
        clip.setStroke(Color.GREEN);
        clip.setFill(Color.TRANSPARENT);

        imageViewRect = new Rectangle(imageView.getX(), imageView.getY(), imageView.getFitWidth(), imageView.getFitHeight());
        imageViewRect.widthProperty().bind(imageView.fitWidthProperty());
        imageViewRect.heightProperty().bind(imageView.fitHeightProperty());
        imageViewRect.xProperty().bind(imageView.xProperty());
        imageViewRect.yProperty().bind(imageView.yProperty());
        clip.setX(imageView.getX() + 10);
        clip.setY(imageView.getY() + 10);
        darkenedArea = Shape.subtract(imageViewRect, clip);
        darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
        anchorPane.getChildren().addAll(imageView, darkenedArea, clip);
        imageView.fitWidthProperty().addListener((ob,old,now)->{
            if(old!=now){
                enSureClipInRec();
                int index = anchorPane.getChildren().indexOf(darkenedArea);
                darkenedArea = Shape.subtract(imageViewRect, new Rectangle(
                        clip.getX(),
                        clip.getY(),
                        clip.getWidth(),
                        clip.getHeight()
                ));
                darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
                anchorPane.getChildren().set(index, darkenedArea);
            }
        });
        imageView.fitHeightProperty().addListener((ob,old,now)->{
            if(old!=now){
                enSureClipInRec();
                int index = anchorPane.getChildren().indexOf(darkenedArea);
                darkenedArea = Shape.subtract(imageViewRect, new Rectangle(
                        clip.getX(),
                        clip.getY(),
                        clip.getWidth(),
                        clip.getHeight()
                ));
                darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
                anchorPane.getChildren().set(index, darkenedArea);
            }
        });

        anchorPane.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            clip.setOnMouseExited(e -> {
                anchorPane.setCursor(Cursor.DEFAULT);
            });

            if (mouseX <= clip.getX() + clip.getWidth() + 5 && mouseX >= clip.getX() + clip.getWidth() - 5 && mouseY <= clip.getY() + clip.getHeight() + 5 && mouseY >= clip.getY() + clip.getHeight() - 5) {
                clip.setCursor(Cursor.NW_RESIZE);
                clipMove(imageView, anchorPane, 0, 0, 1, 1);
            } else if (mouseX >= clip.getX() - 10 && mouseX <= clip.getX() + 10 && mouseY >= clip.getY() - 10 && mouseY <= clip.getY() + 10) {
                //改变鼠标样式
                clip.setCursor(Cursor.NW_RESIZE);
                //监视鼠标移动控制截图区移动
                clipMove(imageView, anchorPane, 1, 1, -1, -1);
            } else if (mouseY <= clip.getY() + 5 && mouseY >= clip.getY() && mouseX <= clip.getX() + clip.getWidth() + 5 && mouseX >= clip.getX() + clip.getWidth() - 5) {
                //右上
                clip.setCursor(Cursor.NE_RESIZE);
                clipMove(imageView, anchorPane, 0, 1, 1, -1);
            } else if (mouseX >= clip.getX() - 10 && mouseX <= clip.getX() + 10 && mouseY <= clip.getY() + clip.getHeight() + 5 && mouseY >= clip.getY() + clip.getHeight() - 5) {
                //左下
                clip.setCursor(Cursor.NE_RESIZE);
                clipMove(imageView, anchorPane, 1, 0, -1, 1);
            } else if (Math.abs(mouseX - clip.getX() - clip.getWidth() / 2) < 3 && Math.abs(mouseY - clip.getY()) < 5) {
                //上
                clip.setCursor(Cursor.N_RESIZE);
                clipMove(imageView, anchorPane, 0, 1, 0, -1);
            } else if (Math.abs(mouseX - clip.getX() - clip.getWidth() / 2) < 3 && Math.abs(mouseY - clip.getY() - clip.getHeight()) < 5) {
                //下
                clip.setCursor(Cursor.S_RESIZE);
                clipMove(imageView, anchorPane, 0, 0, 0, 1);
            } else if (Math.abs(mouseX - clip.getX()) < 3 && Math.abs(mouseY - clip.getY() - clip.getHeight() / 2) < 5) {
                //左
                clip.setCursor(Cursor.W_RESIZE);
                clipMove(imageView, anchorPane, 1, 0, -1, 0);
            } else if (Math.abs(mouseX - clip.getX() - clip.getWidth()) < 3 && Math.abs(mouseY - clip.getY() - clip.getHeight() / 2) < 5) {
                //右
                clip.setCursor(Cursor.E_RESIZE);
                clipMove(imageView, anchorPane, 0, 0, 1, 0);
            } else {
                clip.setCursor(Cursor.MOVE);
                clipMove(imageView, anchorPane, 1, 1, 0, 0);
            }
        });
        //确认或者取消裁剪，逻辑有待修改。
        anchorPane.setOnKeyPressed(event -> {
            if ("Enter".equals(event.getCode().getName())) {
                System.out.println("Enter key pressed");
                anchorPane.getChildren().remove(imageView);
                Rectangle rectangle = new Rectangle(clip.getX(), clip.getY(), clip.getWidth(), clip.getHeight());
                System.out.println(rectangle);
                imageView.setClip(rectangle);
                //保存裁剪图片
                anchorPane.getChildren().add(imageView);
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT); // 设置背景为透明
                params.setViewport(new Rectangle2D(0, 0, image.getWidth(), image.getHeight())); // 设置裁剪区域
                // 调用snapshot方法获取裁剪后的图像
                WritableImage snapshot = anchorPane.snapshot(params, null);
                // 保存裁剪后的图像到本地文件
                File file = new File("cropped_image.png");
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @describle 调整clip裁剪框的移动
     * @author 申雄全
     * @updateTime 2023/12/4 17:40
     */
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

    private static void enSureClipInRec(){
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
                clip.setHeight(imageViewBounds.getHeight());
            }
            if(clipBounds.getWidth()>imageViewBounds.getWidth()){
                clip.setWidth(imageViewBounds.getWidth());
            }

            // 设置新的 clip 位置
            clip.setX(newClipX);
            clip.setY(newClipY);
        }

    }




}




