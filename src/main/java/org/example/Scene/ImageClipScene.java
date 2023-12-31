package org.example.Scene;

import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.layout.Pane;
import org.example.LSMain;
import org.example.Pane.ImagePane;

/**
 * 该类实现裁剪的绘制
 *
 * @author 吴鹄远
 * Date 2023/12/4 17:00
 */
public class ImageClipScene extends SuperScene {

    private static final Pane histogramPane = new Pane();

    private static final ImagePane clipImagePane = new ImagePane() {{
        setWidth(900);
        setHeight(550);
        setLayoutX(60);
        setLayoutY(100);
    }};
    private static final FusionPane modulePane = new FusionPane() {{
        getNode().setPrefWidth(220);
        getNode().setPrefHeight(550);
        getNode().setLayoutX(1100);
        getNode().setLayoutY(350);
    }};
    //创建确认按钮
    private static final FusionButton affirmButton = new FusionButton("确认裁剪") {{
        setLayoutY(30);
        setPrefWidth(130);
        setPrefHeight(50);
    }};
    private static final FusionButton cancelButton = new FusionButton("取消裁剪") {{
        setDisable(true);
        setPrefWidth(130);
        setPrefHeight(50);
    }};
    private static final FusionButton resetButton = new FusionButton("复位") {{
        setDisable(true);
        setPrefWidth(130);
        setPrefHeight(50);
    }};

    public ImageClipScene() {
        super(VSceneRole.MAIN);
        modulePane.getContentPane().getChildren().add(affirmButton);
        modulePane.getContentPane().getChildren().add(cancelButton);
        modulePane.getContentPane().getChildren().add(resetButton);
        //绑定和imagePane的间隔
        modulePane.getNode().layoutXProperty().bind(clipImagePane.layoutXProperty().add(clipImagePane.widthProperty().add(30)));
        modulePane.getNode().layoutYProperty().bind(clipImagePane.layoutYProperty().add(250));
        //绑定layoutX
        histogramPane.layoutXProperty().bind(clipImagePane.layoutXProperty().add(clipImagePane.widthProperty().add(30)));
        //绑定layoutY
        histogramPane.layoutYProperty().bind(clipImagePane.layoutYProperty());


        FXUtils.observeWidthCenter(modulePane.getContentPane(), affirmButton);//使得确认按钮居中显示
        FXUtils.observeWidthHeightCenter(modulePane.getContentPane(), cancelButton);//使得取消按钮居中显示
        //使得取消按钮居中显示
        resetButton.layoutYProperty().bind(modulePane.getContentPane().heightProperty().add(-30 - 50));
        FXUtils.observeWidthCenter(modulePane.getContentPane(), resetButton);
        FXUtils.observeWidthHeight(LSMain.getStage().getInitialScene().getContentPane(), clipImagePane, -350, -200);
        FXUtils.observeHeight(LSMain.getStage().getInitialScene().getContentPane(), modulePane.getNode(), -450);

        getContentPane().getChildren().add(histogramPane);
        getContentPane().getChildren().add(clipImagePane);
        getContentPane().getChildren().add(modulePane.getNode());

    }


    public static ImagePane getClipImagePane() {
        return clipImagePane;
    }

    public static void InitClipImagePane() {
        clipImagePane.InitImagePane();
    }

    public static FusionPane getModulePane() {
        return modulePane;
    }

    public static FusionButton getAffirmButton() {
        return affirmButton;
    }

    public static FusionButton getCancelButton() {
        return cancelButton;
    }

    public static FusionButton getResetButton() {
        return resetButton;
    }

    public static Pane getHistogramPane() {
        return histogramPane;
    }

    @Override
    public String title() {
        return "ImageClip";
    }
}
