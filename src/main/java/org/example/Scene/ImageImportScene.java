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
import io.vproxy.vfx.ui.table.VTableColumn;
import io.vproxy.vfx.ui.table.VTableView;
import io.vproxy.vfx.util.FXUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import org.example.ImageTools.ImportImageResource;
import org.example.LSMain;
import org.example.Obj.AdjustHistory;
import org.example.Obj.ImageObj;
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
        getNode().setLayoutX(70);
        getNode().setLayoutY(100);
        getNode().setPrefWidth(900);
        getNode().setPrefHeight(550);
    }};

    // 创建 FlowPane 用于放图片按钮
    public static FlowPane flowImportPane = new FlowPane() {{
        setLayoutX(5);
        setLayoutY(5);
        setPrefHeight(scrollImportFlowPane.getNode().getPrefHeight());
        setPrefWidth(scrollImportFlowPane.getNode().getPrefWidth());
        // 设置行列间距
        setHgap(50);
        setVgap(25);
    }};
    //新建历史记录表单
   private static VTableView historyTable=new VTableView<AdjustHistory>(){{
        getNode().setPrefHeight(600);
        getNode().setPrefWidth(320);
    }};
   private static VTableColumn<AdjustHistory, String> labelCol=new VTableColumn<AdjustHistory,String>("修改内容", data->data.getAdjustProperty());

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
        //绑定图片按钮显示pane和窗口
        FXUtils.observeWidthHeight(LSMain.getStage().getInitialScene().getContentPane(),scrollImportFlowPane.getNode(),-350,-200);
        //绑定两个pane的宽和高
        FXUtils.observeWidthHeight(scrollImportFlowPane.getNode(), flowImportPane);
        //绑定直方图和editImagePane的距离关系
        //绑定layoutX
        histogramPane.layoutXProperty().bind(scrollImportFlowPane.getNode().layoutXProperty().add(scrollImportFlowPane.getNode().widthProperty().add(30)));
        //绑定layoutY
        histogramPane.layoutYProperty().bind(scrollImportFlowPane.getNode().layoutYProperty());

        // 创建一个矩形用于显示flowPane的边框
        Rectangle flowPaneRec = new Rectangle(scrollImportFlowPane.getNode().getLayoutX(), scrollImportFlowPane.getNode().getLayoutY(), flowImportPane.getPrefWidth() , flowImportPane.getPrefHeight()) {{
            setFill(Color.TRANSPARENT);
            setStroke(Color.WHITE); // 设置矩形的边框颜色
            setStrokeType(StrokeType.OUTSIDE);//边框为内嵌式，不会超出pane的范围
            xProperty().bind(scrollImportFlowPane.getNode().layoutXProperty());
            yProperty().bind(scrollImportFlowPane.getNode().layoutYProperty());
            widthProperty().bind(scrollImportFlowPane.getNode().widthProperty());
            heightProperty().bind(scrollImportFlowPane.getNode().heightProperty());
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

        StaticValues.importHistogramPane(histogramPane);

        getContentPane().getChildren().add(histogramPane);

        //新建一个opPane用于存放文件操作按钮
        FusionPane opPane=new FusionPane(){{
            enableAutoContentWidthHeight();
            getNode().setPrefWidth(220);
            getNode().setPrefHeight(50);
            getNode().setLayoutY(200);
            getNode().setLayoutX(1000);
        }};
        //绑定操作窗口和histogramPane的距离
        opPane.getNode().layoutYProperty().bind(histogramPane.layoutYProperty().add(200));
        //绑定和editImagePane的距离
        opPane.getNode().layoutXProperty().bind(histogramPane.layoutXProperty());

        //删除按钮 用于删除图像
        var deleteBUtton = new FusionButton("删除图片") {{
            setLayoutX(42);
            setPrefWidth(120);
            setPrefHeight(30);
            setOnlyAnimateWhenNotClicked(true);
        }};


        deleteBUtton.setOnAction(e -> {
           if(StaticValues.editingImageObj!=null){
                StaticValues.editingImageObj.delete();
               System.out.println("删除成功");
           }
        });

        //绑定x坐标
        historyTable.getNode().layoutXProperty().bind(opPane.getNode().layoutXProperty());
        historyTable.getNode().layoutYProperty().bind(opPane.getNode().layoutYProperty().add(opPane.getNode().heightProperty()).add(20));
        historyTable.getNode().prefHeightProperty().bind(
                scrollImportFlowPane.getNode().layoutYProperty().add(scrollImportFlowPane.getNode().heightProperty())
                        .subtract(historyTable.getNode().layoutYProperty())
        );
        historyTable.getNode().prefWidthProperty().bind(opPane.getNode().widthProperty());
        historyTable.getColumns().add(labelCol);
        labelCol.setAlignment(Pos.CENTER);

        opPane.getContentPane().getChildren().add(deleteBUtton);
        getContentPane().getChildren().add(opPane.getNode());
        getContentPane().getChildren().add(historyTable.getNode());
        getContentPane().getChildren().add(flowPaneRec);
        getContentPane().getChildren().add(menuBtn);
        getContentPane().getChildren().add(scrollImportFlowPane.getNode());
        scrollImportFlowPane.setContent(flowImportPane);

    }
    public static void renewHistoryTable(){
        ImageObj editingImageObj= StaticValues.editingImageObj;
        historyTable.getItems().clear();
        if(editingImageObj!=null&&!editingImageObj.getAdjustHistory().isEmpty()){
            for(var history:editingImageObj.getAdjustHistory()){
                EditHistoryScene.addLabel(history,historyTable);
            }
        }
    }


    @Override
    public String title() {
        return "ImageImport";
    }

}
