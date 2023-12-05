package org.example.Scene;

import io.vproxy.vfx.ui.scene.VSceneRole;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import org.example.ImageModification.ImageClip;
import org.example.ImageTools.ImportImageResource;
import org.example.StaticValues;

/**
 * @author 吴鹄远
 * @Description
 * @date 2023/12/4 17:00
 */
public class ImageClipScene extends SuperScene{
    public ImageClipScene() {
        super(VSceneRole.MAIN);
        //新建一个pane，用于展示图片
        Pane ImagePane=new Pane(){{
            setPrefWidth(900);
            setPrefHeight(550);
            setLayoutX(100);
            setLayoutY(100);
        }};

        //创建一个矩形，用来包裹ImagePane
        Rectangle ImagePaneRec=new Rectangle(0,0,ImagePane.getPrefWidth()-2,ImagePane.getPrefHeight()-2){{
            setFill(Color.WHITE);
            setStroke(Color.WHITE);
            setStrokeType(StrokeType.INSIDE);
            setLayoutX(ImagePane.getLayoutX());
            setLayoutY(ImagePane.getLayoutY());
        }};
        ImagePane.setClip(ImagePaneRec);

//        if(StaticValues.editingImage!=null){
//            ImageClip.imageClip(StaticValues.editingImage,ImagePane);
//        }
        ImagePane.getChildren().add(new ImageView(ImportImageResource.getInstance().getImage("image/icon.png")));
        getContentPane().getChildren().add(ImagePane);


    }



    @Override
    public String title() {
        return "ImageClip";
    }
}
