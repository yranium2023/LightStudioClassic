package org.example;

import javafx.scene.layout.Pane;
import org.example.Obj.ImageObj;
import org.example.Pane.HistogramPane;

/**
 * 这个类用于存储静态变量
 *
 * @author 吴鹄远
 * Date 2023/12/5 22:00
 */
public class StaticValues {

    public static ImageObj editingImageObj = null;
    public static HistogramPane histogramPane = new HistogramPane() {{
        setWidth(250);
        setHeight(200);
    }};

    public static void importHistogramPane(Pane newPane) {
        if (histogramPane.getParent() != null) {
            Pane curPane = (Pane) histogramPane.getParent();
            curPane.getChildren().remove(histogramPane);
        }
        if (histogramPane.getParent() == null) {
            newPane.getChildren().add(histogramPane);
        }
    }

}
