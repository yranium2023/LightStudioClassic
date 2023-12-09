package org.example.Scene;

import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.example.ImageModification.ImageClip;
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
           getNode().setPrefHeight(500);
           getNode().setLayoutX(1050-50);
           getNode().setLayoutY(250);
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
            if(StaticValues.editingImage!=null){
                sceneGroupSup.get().show(imageClipScene, VSceneShowMethod.FROM_TOP);
                ImageClipScene.InitClipImagePane();
                ImageClip.imageClip(StaticValues.editingImage.getEditingImage(),ImageClipScene.getClipImagePane());
            }
        });
        imageClipPane.getContentPane().getChildren().add(imageClipButton);
        getContentPane().getChildren().add(editingPaneRec);
        getContentPane().getChildren().add(editingPane.getNode());
        editingPane.setContent(imageClipPane.getNode());


        
    }

    @Override
    public String title() {
        return "ImageEdit";
    }
}
