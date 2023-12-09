package org.example.Scene;

import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.pane.FusionPane;
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

    private static FusionPane modulePane=new FusionPane(){{
        getNode().setPrefWidth(180);
        getNode().setPrefHeight(550);
        getNode().setLayoutX(1100);
        getNode().setLayoutY(100);
    }};

    //创建确认按钮
    private static FusionButton affirmButton=new FusionButton("确认裁剪"){{
        setLayoutY(30);
        setPrefWidth(130);
        setPrefHeight(50);
    }};

    public ImageClipScene() {
        super(VSceneRole.MAIN);
        modulePane.getContentPane().getChildren().add(affirmButton);

        modulePane.getNode().layoutXProperty().bind(clipImagePane.layoutXProperty().add(clipImagePane.widthProperty().add(30)));
        getContentPane().getChildren().add(clipImagePane);
        getContentPane().getChildren().add(modulePane.getNode());

        FXUtils.observeWidthCenter(modulePane.getContentPane(),affirmButton);
        FXUtils.observeWidthHeight(LSMain.getStage().getInitialScene().getContentPane(),clipImagePane,-350,-200);
        FXUtils.observeHeight(LSMain.getStage().getInitialScene().getContentPane(),modulePane.getNode(),-200);

    }


    public static ImagePane getClipImagePane(){
        return clipImagePane;
    }
    public static void InitClipImagePane(){
        clipImagePane.InitImagePane();
    }

    public static FusionPane getModulePane() {
        return modulePane;
    }

    public static FusionButton getAffirmButton() {
        return affirmButton;
    }

    @Override
    public String title() {
        return "ImageClip";
    }
}
