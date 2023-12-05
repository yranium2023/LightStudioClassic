package org.example.Scene;

import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneRole;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.function.Supplier;

/**
 * @author 吴鹄远
 * @Description 该场景用于调整图像的基础参数
 * @date 2023/12/3 21:07
 */
public class ImageEditScene extends SuperScene{
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
        Rectangle ImagePaneRec=new Rectangle(0,0,ImagePane.getPrefWidth()-6,ImagePane.getPrefHeight()-6){{
           setStrokeWidth(3);
           setFill(Color.TRANSPARENT);
           setStroke(Color.WHITE);
           setStrokeType(StrokeType.INSIDE);
        }};
        ImagePane.getChildren().add(ImagePaneRec);




        
    }

    @Override
    public String title() {
        return "ImageEdit";
    }
}
