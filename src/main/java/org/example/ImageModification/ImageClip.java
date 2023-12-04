package org.example.Imagemodification;


import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;


/**
 * @author misssing
 * @Description: 此类实现行图片裁剪
 * @date 2023/12/3 20:22
 */

public class ImageClip{
    private static Rectangle clip = new Rectangle(50,50,100,100);
    private static double mouseX;
    private static double mouseY;
    private static Shape darkenedArea;
    /**
     * @describle 此方法实现图片的裁剪
     * @author missing
     * @updateTime 2023/12/4 10:59
     */
    public static void imageClip(Image image,Pane anchorPane) {
        // 创建ImageView并设置图像
        ImageView imageView = new ImageView(image);
        clip.setStrokeWidth(3);
        clip.setStrokeType(StrokeType.CENTERED);
        clip.setStroke(Color.GREEN);
        clip.setFill(Color.TRANSPARENT);
        darkenedArea = Shape.subtract(new Rectangle(0,0,image.getWidth(),image.getHeight()),clip);
        darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
        anchorPane.getChildren().addAll(imageView,darkenedArea,clip);
        anchorPane.setOnMouseMoved(event -> {
            mouseX = event.getX();
            mouseY = event.getY();
            if (mouseX <= clip.getX() + clip.getWidth() + 5 && mouseX >= clip.getX() + clip.getWidth() - 5 && mouseY <= clip.getY() + clip.getHeight() + 5 && mouseY >= clip.getY() + clip.getHeight() - 5) {
                anchorPane.setOnMouseDragged(mouseEvent -> {
                    double deltaX = mouseEvent.getX() - mouseX;
                    double deltaY = mouseEvent.getY() - mouseY;
                    clip.setWidth(clip.getWidth() + deltaX);
                    clip.setHeight(clip.getHeight() + deltaY);
                    mouseX = mouseEvent.getX();
                    mouseY = mouseEvent.getY();
                    darkenedArea = Shape.subtract(new Rectangle(0,0,image.getWidth(),image.getHeight()),clip);
                    darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
                    anchorPane.getChildren().set(1,darkenedArea);
                });
            } else if (mouseX >= clip.getX() - 10 && mouseX <= clip.getX() + 10 && mouseY >= clip.getY() - 10 && mouseY <= clip.getY() + 10) {
                anchorPane.setOnMouseDragged(mouseEvent -> {
                    double deltaX = mouseEvent.getX() - mouseX;
                    double deltaY = mouseEvent.getY() - mouseY;
                    double bottomRightX = clip.getX() + clip.getWidth();
                    double bottomRightY = clip.getY() + clip.getHeight();
                    clip.setX(clip.getX() + deltaX);
                    clip.setY(clip.getY() + deltaY);
                    clip.setWidth(bottomRightX - clip.getX());
                    clip.setHeight(bottomRightY - clip.getY());
                    mouseX = mouseEvent.getX();
                    mouseY = mouseEvent.getY();
                    darkenedArea = Shape.subtract(new Rectangle(0,0,image.getWidth(),image.getHeight()),clip);
                    darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
                    anchorPane.getChildren().set(1,darkenedArea);
                });
            } else {
                anchorPane.setOnMouseDragged(mouseEvent -> {
                    double deltaX = mouseEvent.getX() - mouseX;
                    double deltaY = mouseEvent.getY() - mouseY;
                    clip.setX(clip.getX() + deltaX);
                    clip.setY(clip.getY() + deltaY);
                    mouseX = mouseEvent.getX();
                    mouseY = mouseEvent.getY();
                    darkenedArea = Shape.subtract(new Rectangle(0,0,image.getWidth(),image.getHeight()),clip);
                    darkenedArea.setFill(Color.rgb(0, 0, 0, 0.5)); // 设置为半透明黑色
                    anchorPane.getChildren().set(1,darkenedArea);
                });
            }
        });
        anchorPane.setOnKeyPressed(event -> {
            if ("Enter".equals(event.getCode().getName())) {
                System.out.println("Enter key pressed");
                anchorPane.getChildren().remove(imageView);
                javafx.scene.shape.Rectangle rectangle = new Rectangle(clip.getX(), clip.getY(), clip.getWidth(), clip.getHeight());
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

}




