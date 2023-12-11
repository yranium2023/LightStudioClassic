package org.example.Scene;

import io.vproxy.vfx.control.scroll.ScrollDirection;
import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.button.ImageButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.*;
import io.vproxy.vfx.util.FXUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import org.example.ImageClip.ImageClip;
import org.example.ImageTools.ImageScaler;
import org.example.ImageTools.ImageTransfer;
import org.example.ImageTools.ImportImageResource;
import org.example.LSMain;
import org.example.Pane.ImagePane;
import org.example.StaticValues;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author 吴鹄远
 * @Description 该场景用于调整图像的基础参数
 * @date 2023/12/3 21:07
 */
public class ImageEditScene extends SuperScene{
    private ImageClipScene imageClipScene=new ImageClipScene();
    //新建一个pane，用于存放直方图
    public static Pane histogramPane=new Pane();
    //新建一个pane，用于展示图片
    private static ImagePane editImagePane=new ImagePane(){{
        setWidth(900);
        setHeight(550);
        setLayoutX(60);
        setLayoutY(100);
    }};

    VScene scene = new VScene(VSceneRole.DRAWER_HORIZONTAL);

    public ImageEditScene(Supplier<VSceneGroup> sceneGroupSup) {
        super(VSceneRole.MAIN);

        enableAutoContentWidthHeight();
        FXUtils.observeWidthHeight(LSMain.getStage().getInitialScene().getContentPane(),editImagePane,-350,-200);
        getContentPane().getChildren().add(editImagePane);
        //绑定直方图和editImagePane的距离关系
        //绑定layoutX
        histogramPane.layoutXProperty().bind(editImagePane.layoutXProperty().add(editImagePane.widthProperty().add(30)));
        //绑定layoutY
        histogramPane.layoutYProperty().bind(editImagePane.layoutYProperty());

        //新建一个滑动窗口，用来存放所有的效果控件
        VScrollPane editingPane=new VScrollPane(){{
           enableAutoContentWidthHeight();
           getNode().setPrefWidth(220);
           getNode().setPrefHeight(550);
           getNode().setLayoutX(1050-50);
           getNode().setLayoutY(300);
        }};
        //绑定滑动窗口和histogramPane的距离
        editingPane.getNode().layoutYProperty().bind(histogramPane.layoutYProperty().add(200));
        //绑定和editImagePane的距离
        editingPane.getNode().layoutXProperty().bind(histogramPane.layoutXProperty());
        //绑定和界面下端的距离
        FXUtils.observeHeight(LSMain.getStage().getInitialScene().getContentPane(),editingPane.getNode(),-400);
        //创建一个矩形用来包裹editingPane
        Rectangle editingPaneRec=new Rectangle(
                editingPane.getNode().getLayoutX(),
                editingPane.getNode().getLayoutY(),
                editingPane.getNode().getPrefWidth(),
                editingPane.getNode().getPrefHeight()
        ){{
            xProperty().bind(editingPane.getNode().layoutXProperty());
            yProperty().bind(editingPane.getNode().layoutYProperty());
            widthProperty().bind(editingPane.getNode().widthProperty());
            heightProperty().bind(editingPane.getNode().heightProperty());
            setFill(Color.TRANSPARENT);
            setStroke(Color.WHITE);
            setStrokeType(StrokeType.INSIDE);
        }};
        //创建一个fusionPane用于包裹fusionButton
        FusionPane littleModulePane=new FusionPane(){{
            enableAutoContentWidthHeight();
            getNode().setPrefWidth(editingPane.getNode().getPrefWidth()-50);
            getNode().setPrefHeight(45);
            getNode().setLayoutY(20);
        }};
        //创建绑定，使得ImageClipPane始终位于editingPane中间
        FXUtils.observeWidthCenter(editingPane.getNode(),littleModulePane.getNode());
        //创建一个fusionButton用于前往图像裁剪
        FusionButton imageClipButton=new FusionImageButton(ImportImageResource.getInstance().getImage("image/clip.png")){{
            setPrefWidth(25);
            setPrefHeight(25);
            setOnlyAnimateWhenNotClicked(true);
            setLayoutX(10);
            getImageView().setFitHeight(15);
        }};
        //使得按钮在pane的高度中间
        FXUtils.observeHeightCenter(littleModulePane.getContentPane(),imageClipButton);
        //设置按下后的动作
        imageClipButton.setOnAction(e->{
            if(!sceneGroupSup.get().getScenes().contains(imageClipScene)){
                sceneGroupSup.get().addScene(imageClipScene);
            }
            if(StaticValues.editingImageObj !=null){
                StaticValues.importHistogramPane(imageClipScene.getHistogramPane());
                sceneGroupSup.get().show(imageClipScene, VSceneShowMethod.FROM_TOP);
                ImageClipScene.InitClipImagePane();
                ImageClip.imageClip(StaticValues.editingImageObj,ImageClipScene.getClipImagePane());
                ImageClip.enSureClipInRec();
            }
        });
        littleModulePane.getContentPane().getChildren().add(imageClipButton);
        getContentPane().getChildren().add(editingPaneRec);
        getContentPane().getChildren().add(editingPane.getNode());
        editingPane.setContent(littleModulePane.getNode());
        getContentPane().getChildren().add(histogramPane);

        //生成下方pane
        var navigatePane = new FusionPane();{
            getNode().setPrefHeight(30);
            getNode().setPrefWidth(getContentPane().getWidth());
        }
        navigatePane.getNode().layoutYProperty().bind(LSMain.getStage().getInitialScene().getContentPane().heightProperty().add(-50));
        getContentPane().getChildren().add(navigatePane.getNode());
        FXUtils.observeWidthCenter(LSMain.getStage().getInitialScene().getContentPane(),navigatePane.getNode());
        var fromBottomButton = new FusionImageButton(ImportImageResource.getInstance().getImage("image/upArrow.png")) {{
            setPrefWidth(1200);
            setPrefHeight(30);
            getImageView().setFitHeight(20);
        }};

        // 在初始化部分定义一个 Timeline 用于刷新添加按钮
        Timeline refreshTimeline = new Timeline();
        //初始化scene
        scene.enableAutoContentWidthHeight();
        scene.getNode().setPrefHeight(120);
        scene.getNode().setBackground(new Background(new BackgroundFill(
                Theme.current().subSceneBackgroundColor(),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        //新建VScrollPane用于生成滑动窗口，并存放flowPane
        VScrollPane scrollFlowPane = new VScrollPane(ScrollDirection.HORIZONTAL) {{
            getNode().setLayoutX(0);
            getNode().setLayoutY(0);
            getNode().setPrefWidth(1275);
            getNode().setPrefHeight(100);
        }};
        FXUtils.observeWidthHeight(scene.getContentPane(),scrollFlowPane.getNode());
        // 创建 FlowPane 用于放图片按钮
        HBox hBox = new HBox() {{
            setLayoutX(10);
            setLayoutY(0);
            setPrefHeight(scrollFlowPane.getNode().getPrefHeight());
            setPrefWidth(scrollFlowPane.getNode().getPrefWidth());
            // 设置行间距
        }};
        //绑定两个pane的宽和高
        FXUtils.observeWidthHeight(scrollFlowPane.getNode(), hBox);
        // 创建一个矩形用于显示flowPane的边框
        Rectangle flowPaneRec = new Rectangle(scrollFlowPane.getNode().getLayoutX() , scrollFlowPane.getNode().getLayoutY() , hBox.getPrefWidth() , hBox.getPrefHeight() ) {{
            setFill(Color.TRANSPARENT);
            setStroke(Color.WHITE); // 设置矩形的边框颜色
            setStrokeType(StrokeType.INSIDE);//边框为内嵌式，不会超出pane的范围
        }};
        flowPaneRec.heightProperty().bind(scrollFlowPane.getNode().heightProperty());
        scene.getContentPane().getChildren().add(flowPaneRec);
        scene.getContentPane().getChildren().add(scrollFlowPane.getNode());
        scrollFlowPane.setContent(hBox);
        fromBottomButton.setOnAction(e -> {
            if (!sceneGroupSup.get().getScenes().contains(scene)) {
                sceneGroupSup.get().addScene(scene, VSceneHideMethod.TO_BOTTOM);
            }
             sceneGroupSup.get().show(scene, VSceneShowMethod.FROM_BOTTOM);
            // 启动或重新开始 Timeline 定时器
            refreshTimeline.stop();  // 停止之前的定时器，以免叠加
            //50ms刷新一次
            refreshTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(50), event -> {
                List<FusionButton> fusionImageButtons = ImageImportScene.menuScene.getFusionImageButtons();
                if (fusionImageButtons != null && !fusionImageButtons.isEmpty()) {
                    // 将按钮添加到 hBox
                    for(FusionButton fusionButton:fusionImageButtons){
                        hBox.getChildren().add(fusionButton);
                        hBox.getChildren().add(new HPadding(100));
                    }
                    //清空生成的按钮
                    ImageImportScene.menuScene.clearImageButtons();
                    refreshTimeline.stop();
                }
            }));
            refreshTimeline.setCycleCount(Timeline.INDEFINITE);
            refreshTimeline.play();
        });
        navigatePane.getContentPane().getChildren().add(fromBottomButton);
    }

    public void initEditImagePane(){
        editImagePane.InitImagePane();
        ImageView nowImageView= ImageScaler.getImageView(StaticValues.editingImageObj.getEditingImage(),editImagePane);
        editImagePane.getChildren().add(nowImageView);
    }

    @Override
    public String title() {
        return "ImageEdit";
    }
}
