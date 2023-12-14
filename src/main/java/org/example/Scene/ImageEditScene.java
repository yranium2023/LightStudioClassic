package org.example.Scene;

import io.vproxy.vfx.control.scroll.ScrollDirection;
import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.*;
import io.vproxy.vfx.ui.slider.SliderDirection;
import io.vproxy.vfx.ui.slider.VSlider;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
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
import org.example.Curve.SplineCanvas.SplineBrightnessAdjustment;
import org.example.ImageClip.ImageClip;
import org.example.ImageModification.*;
import org.example.ImageTools.ImageScaler;
import org.example.ImageTools.ImageTransfer;
import org.example.ImageTools.ImportImageResource;
import org.example.LSMain;
import org.example.Pane.ImagePane;
import org.example.StaticValues;

import java.awt.image.BufferedImage;
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
    //创建一个pane来包含各种组件
    private static Pane prePane=new Pane(){{
        setPrefWidth(220);
    }};
    //创建一个fusionPane用于包裹fusionButton
    private static FusionPane littleModulePane=new FusionPane(){{
        getNode().setPrefWidth(200);
        getNode().setPrefHeight(45);
        getNode().setLayoutY(20);
    }};
    VScene scene = new VScene(VSceneRole.DRAWER_HORIZONTAL);

    public static VScrollPane scrollEditFlowPane = new VScrollPane(ScrollDirection.HORIZONTAL) {{
        getNode().setLayoutX(0);
        getNode().setLayoutY(0);
        getNode().setPrefWidth(1275);
        getNode().setPrefHeight(100);
    }};

    // 创建 FlowPane 用于放图片按钮
    public static HBox hEditBox = new HBox() {{
        setLayoutX(10);
        setLayoutY(0);
        setPrefHeight(scrollEditFlowPane.getNode().getPrefHeight());
        setPrefWidth(scrollEditFlowPane.getNode().getPrefWidth());
        // 设置行间距
    }};

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

        //创建绑定，使得ImageClipPane始终位于Pane中间
        FXUtils.observeWidthCenter(prePane,littleModulePane.getNode());
        prePane.getChildren().add(littleModulePane.getNode());
        //创建一个Button用于前往图像裁剪
        FusionImageButton imageClipButton=new FusionImageButton(ImportImageResource.getInstance().getImage("image/clip.png")){{
            setPrefWidth(25);
            setPrefHeight(25);
            setOnlyAnimateWhenNotClicked(true);
            setLayoutX(7);
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

        //创建滑动条
        //创建标签
        var contrastLabel = new ThemeLabel("对比度调整：") {{
            FontManager.get().setFont(this, settings -> settings.setSize(12));
            setLayoutX(10);
            layoutYProperty().bind(littleModulePane.getNode().layoutYProperty().add(littleModulePane.getNode().heightProperty().add(20)));
        }};
        //创建对比度滑动条
        var contrastSlider=new VSlider(SliderDirection.LEFT_TO_RIGHT){{
            setLength(180);
            setPercentage(0.5);
            setValueTransform(value -> {
                double mappedValue = value * 2 - 1; // 将百分比值映射到 [-1, 1] 范围
                return String.format("%.2f", mappedValue);});
            layoutYProperty().bind(contrastLabel.layoutYProperty().add(30));
        }};
        //创建标签
        var exposureLabel = new ThemeLabel("曝光度调整：") {{
            setLayoutX(10);
            FontManager.get().setFont(this, settings -> settings.setSize(12));
            layoutYProperty().bind(contrastSlider.layoutYProperty().add(30));
        }};
        //创建曝光度滑动条
        var exposureSlider=new VSlider(SliderDirection.LEFT_TO_RIGHT){{
            setLength(180);
            setPercentage(0.5);
            setValueTransform(value -> {
                double mappedValue = value * 2 - 1; // 将百分比值映射到 [-1, 1] 范围
                return String.format("%.2f", mappedValue);});
            layoutYProperty().bind(exposureLabel.layoutYProperty().add(30));
        }};
        //创建标签
        var saturationLabel = new ThemeLabel("饱和度调整：") {{
            FontManager.get().setFont(this, settings -> settings.setSize(12));
            setLayoutX(10);
            layoutYProperty().bind(exposureSlider.layoutYProperty().add(30));
        }};
        //创建饱和度滑动条
        var saturationSlider=new VSlider(SliderDirection.LEFT_TO_RIGHT){{
            setLength(180);
            setPercentage(0.5);
            setValueTransform(value -> {
                double mappedValue = value * 2 - 1; // 将百分比值映射到 [-1, 1] 范围
                return String.format("%.2f", mappedValue);});
            layoutYProperty().bind(saturationLabel.layoutYProperty().add(30));
        }};
        //创建标签
        var temperatureLabel = new ThemeLabel("色温调整：") {{
            FontManager.get().setFont(this, settings -> settings.setSize(12));
            setLayoutX(10);
            layoutYProperty().bind(saturationSlider.layoutYProperty().add(30));
        }};
        //创建色温滑动条
        var temperatureSlider=new VSlider(SliderDirection.LEFT_TO_RIGHT){{
            setLength(180);
            setPercentage(0.5);
            setValueTransform(value -> {
                double mappedValue = value * 2 - 1; // 将百分比值映射到 [-1, 1] 范围
                return String.format("%.2f", mappedValue);});
            layoutYProperty().bind(temperatureLabel.layoutYProperty().add(30));
        }};

        //设置居中
        FXUtils.observeWidthCenter(prePane,contrastSlider);
        FXUtils.observeWidthCenter(prePane,exposureSlider);
        FXUtils.observeWidthCenter(prePane,saturationSlider);
        FXUtils.observeWidthCenter(prePane,temperatureSlider);


        //创建一个button用于前往滑动条调整
        var sliderEditButton=new FusionImageButton(
                ImportImageResource.getInstance().getImage("image/slider.png")
        ){{
            setPrefWidth(25);
            setPrefHeight(25);
            setOnlyAnimateWhenNotClicked(true);
            setLayoutX(7+25+10);
            getImageView().setFitHeight(15);
        }};
        sliderEditButton.setOnAction(event -> {
            if(StaticValues.editingImageObj!=null){
                prePane.getChildren().clear();
                ImageAdjustment.bufferedImage = ImageTransfer.toBufferedImage(StaticValues.editingImageObj.getEditingImage());
                ImageAdjustment.processedImage = new BufferedImage(
                        ImageAdjustment.bufferedImage.getWidth(),
                        ImageAdjustment.bufferedImage.getHeight(),
                        BufferedImage.TYPE_INT_ARGB);
                prePane.getChildren().addAll(
                        littleModulePane.getNode(),
                        contrastLabel,
                        contrastSlider,
                        exposureLabel,
                        exposureSlider,
                        saturationLabel,
                        saturationSlider,
                        temperatureLabel,
                        temperatureSlider
                );
                ImageContrastAdjustment.contrastAdjustBind(contrastSlider,StaticValues.editingImageObj);
                ImageExposureAdjustment.exposerAdjustBind(exposureSlider,StaticValues.editingImageObj);
                ImageSaturationAdjustment.saturationAdjustBind(saturationSlider,StaticValues.editingImageObj);
                ImageTemperatureAdjustment.temperatureAdjustBind(temperatureSlider,StaticValues.editingImageObj);
            }
        });
        FXUtils.observeHeightCenter(littleModulePane.getContentPane(),sliderEditButton);
        littleModulePane.getContentPane().getChildren().add(sliderEditButton);

        //新建一个button用于自动白平衡
        var awbButton=new FusionImageButton(
                ImportImageResource.getInstance().getImage("/image/AWB.png")
        ){{
            setPrefWidth(25);
            setPrefHeight(25);
            setOnlyAnimateWhenNotClicked(true);
            setLayoutX(7+25+10+25+10);
            getImageView().setFitHeight(15);
        }};
        FXUtils.observeHeightCenter(littleModulePane.getContentPane(),awbButton);
        littleModulePane.getContentPane().getChildren().add(awbButton);
        //设置按钮按下后的动作
        awbButton.setOnAction(event -> {
            if(StaticValues.editingImageObj!=null){
                AutoWhiteBalance.autoWhiteBalance(StaticValues.editingImageObj);
            }
        });

        //新建一个button用于前往曲线调整
        var curveButton=new FusionImageButton(
                ImportImageResource.getInstance().getImage("/image/curve.png")
        ){{
            setPrefWidth(25);
            setPrefHeight(25);
            setOnlyAnimateWhenNotClicked(true);
            setLayoutX(7+25+10+25+10+25+10);
            getImageView().setFitHeight(15);
        }};
        FXUtils.observeHeightCenter(littleModulePane.getContentPane(),curveButton);
        littleModulePane.getContentPane().getChildren().add(curveButton);
        //创建标签
        var curveLabel = new ThemeLabel("色调曲线：") {{
            FontManager.get().setFont(this, settings -> settings.setSize(12));
            setLayoutX(10);
            layoutYProperty().bind(littleModulePane.getNode().layoutYProperty().add(littleModulePane.getNode().heightProperty().add(20)));
        }};
        //创建一个单独的pane用于容纳矩形
        Pane curvePane=new Pane(){{
            setPrefWidth(200);
            setPrefHeight(200);
            layoutYProperty().bind(curveLabel.layoutYProperty().add(30));
        }};
        FXUtils.observeWidthCenter(prePane,curvePane);
        curveButton.setOnAction(event -> {
            prePane.getChildren().clear();
            curvePane.getChildren().clear();
            prePane.getChildren().addAll(
                    littleModulePane.getNode(),
                    curveLabel,
                    curvePane
            );
            SplineBrightnessAdjustment.addCurve(curvePane,StaticValues.editingImageObj);
        });

        //创建一个button用于前往hsl调整
        var HSLButton=new FusionImageButton(
                ImportImageResource.getInstance().getImage("/image/HSL.png")
        ){{
            setPrefWidth(25);
            setPrefHeight(25);
            setOnlyAnimateWhenNotClicked(true);
            setLayoutX(7+25+10+25+10+25+10+25+10);
            getImageView().setFitHeight(15);
        }};
        FXUtils.observeHeightCenter(littleModulePane.getContentPane(),HSLButton);
        littleModulePane.getContentPane().getChildren().add(HSLButton);




        getContentPane().getChildren().add(editingPaneRec);
        getContentPane().getChildren().add(editingPane.getNode());
        editingPane.setContent(prePane);
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
        FXUtils.observeWidthHeight(scene.getContentPane(), scrollEditFlowPane.getNode());
        //绑定两个pane的宽和高
        FXUtils.observeWidthHeight(scrollEditFlowPane.getNode(), hEditBox);
        // 创建一个矩形用于显示flowPane的边框
        Rectangle flowPaneRec = new Rectangle(scrollEditFlowPane.getNode().getLayoutX() , scrollEditFlowPane.getNode().getLayoutY() , hEditBox.getPrefWidth() , hEditBox.getPrefHeight() ) {{
            setFill(Color.TRANSPARENT);
            setStroke(Color.WHITE); // 设置矩形的边框颜色
            setStrokeType(StrokeType.INSIDE);//边框为内嵌式，不会超出pane的范围
        }};
        flowPaneRec.heightProperty().bind(scrollEditFlowPane.getNode().heightProperty());
        scene.getContentPane().getChildren().add(flowPaneRec);
        scene.getContentPane().getChildren().add(scrollEditFlowPane.getNode());
        scrollEditFlowPane.setContent(hEditBox);
        fromBottomButton.setOnAction(e -> {
            if (!sceneGroupSup.get().getScenes().contains(scene)) {
                sceneGroupSup.get().addScene(scene, VSceneHideMethod.TO_BOTTOM);
            }
             sceneGroupSup.get().show(scene, VSceneShowMethod.FROM_BOTTOM);
            // 启动或重新开始 Timeline 定时器
            refreshTimeline.stop();  // 停止之前的定时器，以免叠加
            //50ms刷新一次
            refreshTimeline.getKeyFrames().setAll(new KeyFrame(Duration.millis(50), event -> {
                List<VBox> fusionImageButtons = ImageImportScene.menuScene.getCopyImageButtonsVboxButtons();
                if (fusionImageButtons != null && !fusionImageButtons.isEmpty()) {
                    // 将按钮添加到 hBox
                    for(VBox fusionButton:fusionImageButtons){
                        hEditBox.getChildren().add(fusionButton);
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

    public static void initEditImagePane(){
        editImagePane.InitImagePane();
        if(StaticValues.editingImageObj!=null){
            ImageView nowImageView= ImageScaler.getImageView(StaticValues.editingImageObj.getEditingImage(),editImagePane);
            editImagePane.getChildren().add(nowImageView);
        }
    }

    public static void initImageEditScene(){
        editImagePane.InitImagePane();
        if(StaticValues.editingImageObj!=null){
            ImageView nowImageView= ImageScaler.getImageView(StaticValues.editingImageObj.getEditingImage(),editImagePane);
            editImagePane.getChildren().add(nowImageView);
            prePane.getChildren().clear();
            prePane.getChildren().add(littleModulePane.getNode());
        }
    }

    @Override
    public String title() {
        return "ImageEdit";
    }
}
