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
    //创建矩形包裹pane
    private Rectangle histogramPaneRec =new Rectangle(0,0,getWidth(),getHeight()){{
        setStrokeWidth(2);
        setFill(Color.rgb(188,196,188,0.4));
        setStroke(Color.WHITE);
        setStrokeType(StrokeType.INSIDE);
    }};
    public HistogramPane(){

        setMaxSize(250,200);
        getChildren().add(histogramPaneRec);
        widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v=now.doubleValue();
            histogramPaneRec.setWidth(v);
        });
        heightProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            double v=now.doubleValue();
            histogramPaneRec.setHeight(v);
        });
    }

    public void initHistogramPane(){
        getChildren().clear();
        getChildren().add(histogramPaneRec);
    }

}
