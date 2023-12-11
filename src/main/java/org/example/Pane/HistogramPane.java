package org.example.Pane;

import io.vproxy.vfx.util.FXUtils;
import javafx.scene.chart.AreaChart;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * @author 吴鹄远
 * @Description:
 * @date 2023/12/10 20:29
 */
public class HistogramPane extends Pane {
    //创建一个矩形来裁剪区域，防止轴超出边界
    private Rectangle histogramPaneClipRec=new Rectangle(0,0,getPrefWidth(),getPrefHeight()){{
        setFill(Color.WHITE);
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
    }};
    //创建矩形包裹pane
    private Rectangle histogramPaneRec =new Rectangle(0,0,getWidth(),getHeight()){{
        setStrokeWidth(2);
//        setFill(Color.rgb(188,196,188,0.4));
        setFill(Color.TRANSPARENT);
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
    }};
    public HistogramPane(){

        setMaxSize(220,170);
        getChildren().add(histogramPaneRec);
        widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v=now.doubleValue();
            histogramPaneRec.setWidth(v);
            histogramPaneClipRec.setWidth(v);
        });
        heightProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v=now.doubleValue();
            histogramPaneRec.setHeight(v);
            histogramPaneClipRec.setHeight(v);
        });
    }

    public void initHistogramPane(){
        getChildren().clear();
        setClip(histogramPaneClipRec);
        getChildren().add(histogramPaneRec);
    }

}
