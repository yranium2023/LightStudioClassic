package org.example.ImagePane;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class ImagePane extends Pane {
    private Rectangle ImagePaneRec=new Rectangle(0,0,getPrefWidth(),getPrefHeight()){{
        setFill(Color.WHITE);
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
    }};
    private Rectangle ImagePaneStrokeRec=new Rectangle(0,0,getPrefWidth(),getPrefHeight()){{
        setStrokeWidth(2);
        setFill(Color.rgb(255,255,255,0.6));
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
    }};
    public ImagePane(){
        widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v=now.doubleValue();
            ImagePaneRec.setWidth(v);
            ImagePaneStrokeRec.setWidth(v);
        });
        heightProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v=now.doubleValue();
            ImagePaneRec.setHeight(v);
            ImagePaneStrokeRec.setHeight(v);
        });
    }

    public void InitImagePane(){
        getChildren().clear();
        setClip(ImagePaneRec);
        getChildren().add(ImagePaneStrokeRec);
    }

}
