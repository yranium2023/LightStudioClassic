package org.example.Pane;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * 直方图绘制
 *
 * @author 吴鹄远
 * Date 2023/12/10 20:29
 */
public class HistogramPane extends Pane {

    private final Rectangle histogramPaneClipRec = new Rectangle(0, 0, getPrefWidth(), getPrefHeight()) {{
        setFill(Color.WHITE);
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
    }};

    private final Rectangle histogramPaneRec = new Rectangle(0, 0, getWidth(), getHeight()) {{
        setStrokeWidth(2);
//        setFill(Color.rgb(188,196,188,0.4));
        setFill(Color.TRANSPARENT);
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
    }};

    public HistogramPane() {

        setMaxSize(220, 170);
        getChildren().add(histogramPaneRec);
        widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v = now.doubleValue();
            histogramPaneRec.setWidth(v);
            histogramPaneClipRec.setWidth(v);
        });
        heightProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v = now.doubleValue();
            histogramPaneRec.setHeight(v);
            histogramPaneClipRec.setHeight(v);
        });
    }

    public void initHistogramPane() {
        getChildren().clear();
        setClip(histogramPaneClipRec);
        getChildren().add(histogramPaneRec);
    }

}
