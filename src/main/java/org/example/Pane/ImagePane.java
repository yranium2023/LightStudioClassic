package org.example.Pane;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * 这个类用于生成一个有rec划定显示区域和边框的pane
 *
 * @author 吴鹄远
 * Date 2023/12/9 13:57
 */
public class ImagePane extends Pane {
    private final Rectangle ImagePaneRec = new Rectangle(0, 0, getPrefWidth(), getPrefHeight()) {{
        setFill(Color.WHITE);
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
    }};
    private final Rectangle ImagePaneStrokeRec = new Rectangle(0, 0, getPrefWidth(), getPrefHeight()) {{
        setStrokeWidth(2);
        setFill(Color.rgb(188, 196, 188, 0.4));
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
    }};

    public ImagePane() {
        widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v = now.doubleValue();
            ImagePaneRec.setWidth(v);
            ImagePaneStrokeRec.setWidth(v);
        });
        heightProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v = now.doubleValue();
            ImagePaneRec.setHeight(v);
            ImagePaneStrokeRec.setHeight(v);
        });
    }

    /**
     * 实现对Pane的初始化操作
     *
     * @author 吴鹄远
     * Date 2023/12/9 13:59
     **/

    public void InitImagePane() {
        getChildren().clear();
        setClip(ImagePaneRec);
        getChildren().add(ImagePaneStrokeRec);
    }

}
