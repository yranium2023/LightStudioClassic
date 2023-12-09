package org.example.Scene;

import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.*;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.example.ImageModification.ImageClip;
import org.example.LSMain;
import org.example.StaticValues;

import java.util.function.Supplier;

/**
 * @author 吴鹄远
 * @Description 该场景用于调整图像的基础参数
 * @date 2023/12/3 21:07
 */
public class ImageEditScene extends SuperScene{
    private ImageClipScene imageClipScene=new ImageClipScene();
    public ImageEditScene(Supplier<VSceneGroup> sceneGroupSup) {
        super(VSceneRole.MAIN);

        enableAutoContentWidthHeight();
        //新建一个pane，用于展示图片
        Pane ImagePane=new Pane(){{
           setPrefWidth(900);
           setPrefHeight(550);
           setLayoutX(100);
           setLayoutY(100);
        }};
        getContentPane().getChildren().add(ImagePane);

        //创建一个矩形，用来包裹ImagePane
        Rectangle ImagePaneRec=new Rectangle(0,0,ImagePane.getPrefWidth()-2,ImagePane.getPrefHeight()-2){{
           setFill(Color.TRANSPARENT);
           setStroke(Color.WHITE);
           setStrokeType(StrokeType.INSIDE);
        }};
        ImagePane.getChildren().add(ImagePaneRec);

        //新建一个滑动窗口，用来存放所有的效果控件
        VScrollPane editingPane=new VScrollPane(){{
           enableAutoContentWidthHeight();
           getNode().setPrefWidth(250);
           getNode().setPrefHeight(550);
           getNode().setLayoutX(1050-50);
           getNode().setLayoutY(100);
        }};
        //创建一个矩形用来包裹editingPane
        Rectangle editingPaneRec=new Rectangle(
                editingPane.getNode().getLayoutX(),
                editingPane.getNode().getLayoutY(),
                editingPane.getNode().getPrefWidth(),
                editingPane.getNode().getPrefHeight()
        ){{
            setFill(Color.TRANSPARENT);
            setStroke(Color.WHITE);
            setStrokeType(StrokeType.INSIDE);
        }};
        //创建一个fusionPane用于包裹fusionButton
        FusionPane imageClipPane=new FusionPane(){{
            enableAutoContentWidthHeight();
            getNode().setPrefWidth(editingPane.getNode().getPrefWidth()-50);
            getNode().setPrefHeight(60);
            getNode().setLayoutY(20);
        }};
        //创建绑定，使得ImageClipPane始终位于editingPane中间
        FXUtils.observeWidthCenter(editingPane.getNode(),imageClipPane.getNode());
        //创建一个fusionButton用于前往图像裁剪
        FusionButton imageClipButton=new FusionButton("裁剪图像"){{
            setPrefWidth(100);
            setPrefHeight(imageClipPane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
            setOnlyAnimateWhenNotClicked(true);
            setLayoutX(imageClipPane.getNode().getPrefWidth()/2-50);
        }};
//        FXUtils.observeWidth(imageClipPane.getNode(),imageClipButton,-imageClipButton.getWidth()/2);
        imageClipButton.setOnAction(e->{
            if(!sceneGroupSup.get().getScenes().contains(imageClipScene)){
                sceneGroupSup.get().addScene(imageClipScene);
            }
            if(StaticValues.editingImageObj !=null){
                sceneGroupSup.get().show(imageClipScene, VSceneShowMethod.FROM_TOP);
                ImageClipScene.InitClipImagePane();
                ImageClip.imageClip(StaticValues.editingImageObj,ImageClipScene.getClipImagePane(),ImageClipScene.getModulePane());
            }
        });
        imageClipPane.getContentPane().getChildren().add(imageClipButton);
        getContentPane().getChildren().add(editingPaneRec);
        getContentPane().getChildren().add(editingPane.getNode());
        editingPane.setContent(imageClipPane.getNode());

        //生成下方pane
        var navigatePane = new FusionPane();{
            getNode().setPrefHeight(30);
            getNode().setPrefWidth(getContentPane().getWidth());
        }
        navigatePane.getNode().layoutYProperty().bind(LSMain.getStage().getInitialScene().getContentPane().heightProperty().add(-50));
        getContentPane().getChildren().add(navigatePane.getNode());
        FXUtils.observeWidthCenter(LSMain.getStage().getInitialScene().getContentPane(),navigatePane.getNode());
        var fromBottomButton = new FusionButton("选择编辑图片") {{
            setPrefWidth(1200);
            setPrefHeight(30);
        }};
        fromBottomButton.setOnAction(e -> {
            var scene = new VScene(VSceneRole.DRAWER_HORIZONTAL);
            scene.enableAutoContentWidthHeight();
            scene.getNode().setPrefHeight(100);
            scene.getNode().setBackground(new Background(new BackgroundFill(
                    Theme.current().subSceneBackgroundColor(),
                    CornerRadii.EMPTY,
                    Insets.EMPTY
            )));
            var closeBtn = new FusionButton("hide") {{
                setPrefWidth(100);
                setPrefHeight(50);
            }};
            closeBtn.setOnAction(ee -> {
                sceneGroupSup.get().hide(scene, VSceneHideMethod.TO_BOTTOM);
                FXUtils.runDelay(VScene.ANIMATION_DURATION_MILLIS, () -> sceneGroupSup.get().removeScene(scene));
            });
            scene.getContentPane().getChildren().add(closeBtn);
            FXUtils.observeWidthHeightCenter(scene.getContentPane(), closeBtn);
            sceneGroupSup.get().addScene(scene, VSceneHideMethod.TO_BOTTOM);
            FXUtils.runDelay(50, () -> sceneGroupSup.get().show(scene, VSceneShowMethod.FROM_BOTTOM));
        });
        navigatePane.getContentPane().getChildren().add(fromBottomButton);
    }

    @Override
    public String title() {
        return "ImageEdit";
    }
}
