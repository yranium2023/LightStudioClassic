package org.example.Scene;

import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneHideMethod;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.util.FXUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import org.example.ImageTools.ImportImageResource;
import org.example.StaticValues;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author 张喆宇
 * @Description: 用于文件管理 暂时用于测试
 * @date 2023/12/3 20:56
 */
public class ImageImportScene extends SuperScene {

    public static ImageImportMenuScene menuScene = null;
    public static Pane histogramPane=new Pane();

    public static VScrollPane scrollImportFlowPane = new VScrollPane() {{
        getNode().setLayoutX(100);
        getNode().setLayoutY(100);
        getNode().setPrefWidth(850);
        getNode().setPrefHeight(550);
    }};

    // 创建 FlowPane 用于放图片按钮
    public static FlowPane flowImportPane = new FlowPane() {{
        setLayoutX(0);
        setLayoutY(0);
        setPrefHeight(scrollImportFlowPane.getNode().getPrefHeight());
        setPrefWidth(scrollImportFlowPane.getNode().getPrefWidth());
        // 设置行列间距
        setHgap(50);
        setVgap(25);
    }};


    public ImageImportScene(Supplier<VSceneGroup> sceneGroupSup) {
        super(VSceneRole.MAIN);
        menuScene=new ImageImportMenuScene(sceneGroupSup);
        enableAutoContentWidthHeight();
        //创建左上角的menuButton
        var menuBtn = new FusionImageButton(ImportImageResource.getInstance().getImage("image/menu.png")) {{
            setPrefWidth(40);
            setPrefHeight(VStage.TITLE_BAR_HEIGHT + 1);
            getImageView().setFitHeight(15);
            setLayoutX(-2);
            setLayoutY(-1);
        }};
        //新建VScrollPane用于生成滑动窗口，并存放flowPane

        //绑定两个pane的宽和高
        FXUtils.observeWidthHeight(scrollImportFlowPane.getNode(), flowImportPane);


        // 创建一个矩形用于显示flowPane的边框
        Rectangle flowPaneRec = new Rectangle(scrollImportFlowPane.getNode().getLayoutX() - 20, scrollImportFlowPane.getNode().getLayoutY() - 10, flowImportPane.getPrefWidth() + 20, flowImportPane.getPrefHeight() + 10) {{
            setFill(Color.TRANSPARENT);
            setStroke(Color.WHITE); // 设置矩形的边框颜色
            setStrokeType(StrokeType.INSIDE);//边框为内嵌式，不会超出pane的范围
        }};
        menuScene.enableAutoContentWidthHeight();

        // 在初始化部分定义一个 Timeline 用于刷新添加按钮
        Timeline refreshTimeline = new Timeline();

        menuBtn.setOnAction(e -> {
            if (!sceneGroupSup.get().getScenes().contains(menuScene)) {
                sceneGroupSup.get().addScene(menuScene, VSceneHideMethod.TO_LEFT);
            }
            sceneGroupSup.get().show(menuScene, VSceneShowMethod.FROM_LEFT);
            // 启动或重新开始 Timeline 定时器
            refreshTimeline.stop();  // 停止之前的定时器，以免叠加
            //50ms刷新一次
            refreshTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(50), event -> {
                List<VBox> fusionImageButtons = menuScene.getFusionImageButtonsVbox();
                if (fusionImageButtons != null && !fusionImageButtons.isEmpty()) {
                    // 将按钮添加到 FlowPane
                    flowImportPane.getChildren().addAll(fusionImageButtons);
                    //清空生成的按钮
                    menuScene.clearImageButtonsVbox();
                    refreshTimeline.stop();
                }
            }));
            refreshTimeline.setCycleCount(Timeline.INDEFINITE);
            refreshTimeline.play();
        });

        histogramPane.layoutXProperty().bind(scrollImportFlowPane.getNode().layoutXProperty().add(scrollImportFlowPane.getNode().widthProperty().add(60)));
        histogramPane.layoutYProperty().bind(scrollImportFlowPane.getNode().layoutYProperty());
        StaticValues.importHistogramPane(histogramPane);

        getContentPane().getChildren().add(histogramPane);

        //新建一个opPane用于存放文件操作按钮
        FusionPane opPane=new FusionPane(){{
            getNode().setPrefWidth(220);
            getNode().setPrefHeight(370);
            getNode().setLayoutY(280);
            getNode().setLayoutX(1010);
        }};
        //删除按钮 用于删除图像
        var deleteBUtton = new FusionButton("删除图片") {{
            setLayoutX(40);
            setPrefWidth(125);
            setPrefHeight(50);
            setOnlyAnimateWhenNotClicked(true);
        }};

        deleteBUtton.setOnAction(e -> {
           if(StaticValues.editingImageObj!=null){
                StaticValues.editingImageObj.delete();
               System.out.println("删除成功");
           }
        });
        opPane.getContentPane().getChildren().add(deleteBUtton);
        getContentPane().getChildren().add(opPane.getNode());
        getContentPane().getChildren().add(flowPaneRec);
        getContentPane().getChildren().add(menuBtn);
        getContentPane().getChildren().add(scrollImportFlowPane.getNode());
        scrollImportFlowPane.setContent(flowImportPane);

    }



    @Override
    public String title() {
        return "ImageImport";
    }

}
