package org.example.Scene;

import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.example.ImageModification.ImageClip;
import org.example.ImagePane.ImagePane;
import org.example.ImageTools.ImportImageResource;
import org.example.LSMain;
import org.example.StaticValues;

/**
 * @author 吴鹄远
 * @Description
 * @date 2023/12/4 17:00
 */
public class ImageClipScene extends SuperScene{
    //新建一个pane，用于展示图片
    private static ImagePane clipImagePane=new ImagePane(){{

            setPrefWidth(900);
            setPrefHeight(550);
            setLayoutX(100);
            setLayoutY(100);
    }};

    public ImageClipScene() {
        super(VSceneRole.MAIN);
        getContentPane().getChildren().add(clipImagePane);
        FXUtils.observeWidthHeight(LSMain.getStage().getInitialScene().getContentPane(),clipImagePane,-300,-200);

    }


    public static ImagePane getClipImagePane(){
        return clipImagePane;
    }
    public static void InitClipImagePane(){
        clipImagePane.InitImagePane();
    }
    @Override
    public String title() {
        return "ImageClip";
    }
}
