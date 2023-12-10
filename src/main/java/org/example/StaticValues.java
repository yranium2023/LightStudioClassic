package org.example;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.example.ImageStatistics.Histogram;
import org.example.Obj.ImageObj;
import org.example.Pane.HistogramPane;

/**
 * @author 吴鹄远
 * @Description 这个类用于存储静态变量
 * @date 2023/12/5 22:00
 */
public class StaticValues {
    //全局变量 直接用 要编辑的图像
    public static ImageObj editingImageObj =null;
    public static HistogramPane histogramPane=new HistogramPane(){{
        setWidth(250);
        setHeight(200);
    }};

    public static void importHistogramPane(Pane newPane){
        if(histogramPane.getParent()!=null){
            Pane curPane=(Pane) histogramPane.getParent();
            curPane.getChildren().remove(histogramPane);
        }
        if(histogramPane.getParent()==null){
            newPane.getChildren().add(histogramPane);
        }
    }

}
