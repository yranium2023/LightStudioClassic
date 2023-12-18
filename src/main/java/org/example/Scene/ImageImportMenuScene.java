package org.example.Scene;

import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.loading.VProgressBar;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.*;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.util.FXUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.example.ImageStatistics.Histogram;
import org.example.ImageTools.ConvertUtil;
import org.example.Obj.ImageObj;
import org.example.Obj.ImportHistory;
import org.example.StaticValues;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author 吴鹄远
 * @Description 这个类用于创建导入图像、查看导入历史的场景
 * @date 2023/12/4 14:37
 */
public class ImageImportMenuScene extends SuperScene {
    //所有所选中的图片
    private List<ImageObj> selectedImages = new ArrayList<>();
    public static List<ImageObj> totalImages = new ArrayList<>();
    private List<VBox> fusionImageButtonsVbox = null;

    public static List<VBox> copyImageButtonsVbox = new ArrayList<>();

    public static List<ImportHistory> importHistories = new ArrayList<>();
    //输出情况 默认为1 1为高品质 0为低品质
    int outputState=1;

    public ImageImportMenuScene(Supplier<VSceneGroup> sceneGroupSup) {

        super(VSceneRole.DRAWER_VERTICAL);
        getNode().setPrefWidth(350);
        enableAutoContentWidth();
        getNode().setBackground(new Background(new BackgroundFill(
                Theme.current().subSceneBackgroundColor(),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        var IOpane = new FusionPane() {{
            getNode().setPrefHeight(50);
            getNode().setPrefWidth(300);
            getNode().setLayoutX(25);
            getNode().setLayoutY(700);
        }};
        getContentPane().getChildren().add(IOpane.getNode());
        FusionButton ImageImportButton = new FusionButton("导入图片") {{
            setPrefWidth(125);
            setPrefHeight(IOpane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
            setOnlyAnimateWhenNotClicked(true);
        }};
        FusionButton OutPutButton = new FusionButton("导出图片") {{
            setPrefWidth(125);
            setPrefHeight(IOpane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
            setLayoutX(155);
            setOnlyAnimateWhenNotClicked(true);
        }};

        IOpane.getContentPane().getChildren().add(ImageImportButton);
        IOpane.getContentPane().getChildren().add(OutPutButton);


        ImageImportButton.setOnAction(e -> {
            // 创建文件选择器
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择图片");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // 设置初始目录

            // 添加文件过滤器，限定选择的文件类型为图片
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.bmp", "*.jpeg")
            );

            // 显示文件选择器对话框并获取选中的多个文件
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

            if (selectedFiles != null && !selectedFiles.isEmpty()) {

                Label label =new Label();
                label.setTextFill(Color.WHITE);
                VProgressBar progressBar = new VProgressBar();
                progressBar.setLength(400);
                VBox vBox = new VBox(
                        label,
                        new VPadding(10),
                        progressBar
                );


                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        int totalFiles = selectedFiles.size();
                        for (int i = 0; i < totalFiles; i++) {
                            // 处理每个选中的图片文件
                            File selectedFile = selectedFiles.get(i);
                            Platform.runLater(() -> label.setText(selectedFile.getName()));
                            String imagePath = selectedFile.toURI().toString();
                            Image selectedImage = new Image(imagePath);
                            ImageObj imageObj = new ImageObj(selectedImage);
                            imageObj.setImageName(selectedFile.getName());
                            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(selectedImage, null);
                            double selectedImageHeight = selectedImage.getHeight();
                            double selectedImageWidth = selectedImage.getWidth();
                            if (selectedImageHeight > 2000 || selectedImageWidth > 2000) {
                                double rate = selectedImageHeight / selectedImageWidth;
                                double editorHeight = 2000;
                                double editorWidth = 2000;
                                if (rate > 1) {
                                    editorWidth = 2000 / rate;
                                } else {
                                    editorHeight = 2000 * rate;
                                }
                                BufferedImage compressedEditorBufferedImage = ConvertUtil.resetSize(bufferedImage, editorWidth, editorHeight, true);
                                Image compressedEditorImage = ConvertUtil.ConvertToFxImage(compressedEditorBufferedImage);
                                imageObj.setEditingImage(compressedEditorImage);
                                selectedImages.add(imageObj);
                                totalImages.add(imageObj);
                            } else {
                                imageObj.setEditingImage(selectedImage);
                                selectedImages.add(imageObj);
                                totalImages.add(imageObj);
                            }

                            // 更新进度
                            progressBar.setProgress((double)(i+1)/(double)(totalFiles));
                        }
                        return null;
                    }
                };

                // 启动任务
                new Thread(task).start();
                VStage stage = new VStage();
                stage.getStage().setResizable(false);
                stage.getStage().setHeight(110);
                stage.getStage().setWidth(500);
                stage.getInitialScene().getContentPane().getChildren().add(vBox);
                vBox.setAlignment(Pos.CENTER);
                vBox.setLayoutX(50);
                vBox.setLayoutY(25);
                stage.show();
                // 设置任务完成时的回调
                task.setOnSucceeded(event -> {
                    // 在 JavaFX 线程上更新 UI
                    Platform.runLater(() -> {
                        // 生成特殊的按钮
                        fusionImageButtonsVbox = createImageButtonsVbox();
                        // 清空之前选中的图片
                        selectedImages.clear();
                        System.out.println("图片全部传入成功");
                        stage.close();
                        sceneGroupSup.get().hide(this, VSceneHideMethod.TO_LEFT);
                        FXUtils.runDelay(VScene.ANIMATION_DURATION_MILLIS, () -> sceneGroupSup.get().removeScene(this));
                    });
                });
            }
        });
        OutPutButton.setOnAction(e -> {
            VStage stage = new VStage();
            stage.getStage().setResizable(false);
            stage.getStage().setHeight(610);
            stage.getStage().setWidth(900);
            VScrollPane scrollOutputFlowPane = new VScrollPane() {{
                getNode().setLayoutX(70);
                getNode().setLayoutY(70);
                getNode().setPrefWidth(760);
                getNode().setPrefHeight(450);
            }};
            FlowPane flowOutputPane = new FlowPane() {{
                setLayoutX(0);
                setLayoutY(0);
                setPrefHeight(scrollOutputFlowPane.getNode().getPrefHeight());
                setPrefWidth(scrollOutputFlowPane.getNode().getPrefWidth());
                // 设置行列间距
                setHgap(50);
                setVgap(25);
            }};
            // 创建一个矩形用于显示flowPane的边框
            Rectangle flowPaneRec = new Rectangle(scrollOutputFlowPane.getNode().getLayoutX() - 20, scrollOutputFlowPane.getNode().getLayoutY() - 10, flowOutputPane.getPrefWidth() + 20, flowOutputPane.getPrefHeight() + 10) {{
                setFill(Color.TRANSPARENT);
                setStroke(Color.WHITE); // 设置矩形的边框颜色
                setStrokeType(StrokeType.INSIDE);//边框为内嵌式，不会超出pane的范围
            }};
            //绑定两个pane的宽和高
            FXUtils.observeWidthHeight(scrollOutputFlowPane.getNode(), flowOutputPane);
            scrollOutputFlowPane.setContent(flowOutputPane);
            List<ImageObj> outImages=new ArrayList<>();
            for(ImageObj imageObj:totalImages){
                VBox outVox = new VBox();
                FusionImageButton outImageButton = new FusionImageButton();
                imageObj.setOutPutImageVBox(outVox);
                outVox.getChildren().add(outImageButton);
                Label descriptionLabel;
                if(imageObj.getClipImages().isEmpty())
                    descriptionLabel = new Label(Integer.toString((int) imageObj.getOriginalImage().getWidth()) + '×' + (int) imageObj.getOriginalImage().getHeight());
                else{
                    descriptionLabel = new Label(Integer.toString((int) imageObj.getClipImages().get(imageObj.getClipImages().size()-1).getWidth()) + '×' + (int) imageObj.getClipImages().get(imageObj.getClipImages().size()-1).getHeight());
                }
                descriptionLabel.setTextFill(Color.WHITE);
                outVox.getChildren().add(descriptionLabel);
                outVox.setAlignment(Pos.CENTER); // 居中对齐
                outVox.setSpacing(5);
                outImageButton.setPrefSize(80,80);
                outImageButton.getImageView().setImage(imageObj.getButtonImage());
                outImageButton.setOnAction(event -> {
                    if(outImages.contains(imageObj)){
                        outImages.remove(imageObj);
                        outVox.setBackground(null);
                    }else{
                        outImages.add(imageObj);
                        outVox.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5), null)));
                    }
                });
                flowOutputPane.getChildren().add(outVox);
            }

            var OPpane = new FusionPane() {{
                getNode().setPrefHeight(50);
                getNode().setPrefWidth(300);
                getNode().setLayoutX(530);
                getNode().setLayoutY(530);
            }};
            FusionButton yesButton = new FusionButton("确定") {{
                setPrefWidth(125);
                setPrefHeight(OPpane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
                setOnlyAnimateWhenNotClicked(true);
            }};
            FusionButton noButton = new FusionButton("撤销") {{
                setPrefWidth(125);
                setPrefHeight(OPpane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
                setLayoutX(155);
                setOnlyAnimateWhenNotClicked(true);
            }};
            yesButton.setOnAction(event -> {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle("选择导出路径");

                // 显示文件选择对话框
                File selectedDirectory = directoryChooser.showDialog(stage.getStage());
                for(ImageObj imageObj:outImages){
                    // 构造完整的文件路径
                    String filePath =selectedDirectory.getPath()+File.separator + imageObj.getImageName();
                    BufferedImage bufferedImage;
                    if(outputState==0)
                        bufferedImage = SwingFXUtils.fromFXImage(imageObj.getEditingImage(),null);
                    else{
                        if(imageObj.getAdjustHistory().isEmpty())
                        {
                            bufferedImage=SwingFXUtils.fromFXImage(imageObj.getOriginalImage(),null);
                        }else{
                            bufferedImage = SwingFXUtils.fromFXImage(imageObj.AdjustRealImage(),null);
                        }
                    }

                    // 保存BufferedImage到文件
                    try {
                        File file = new File(filePath);
                        int lastDotIndex = filePath.lastIndexOf('.');
                        // 获取文件拓展名
                        String fileExtension =filePath.substring(lastDotIndex + 1);
                        ImageIO.write(bufferedImage, fileExtension, file); // 这里可以根据需要选择其他图片格式
                        System.out.println("Image saved to: " + file.getAbsolutePath());
                    } catch (IOException event1) {
                        event1.printStackTrace();
                    }
                }
                stage.close();
            });
            noButton.setOnAction(event -> {
                for(ImageObj imageObj:outImages){
                    imageObj.getOutPutImageVBox().setBackground(null);
                }
                outImages.clear();
            });
            OPpane.getContentPane().getChildren().add(yesButton);
            OPpane.getContentPane().getChildren().add(noButton);

            var qualityPane = new FusionPane() {{
                getNode().setPrefHeight(50);
                getNode().setPrefWidth(300);
                getNode().setLayoutX(70);
                getNode().setLayoutY(530);
            }};
            FusionButton highButton = new FusionButton("高品质") {{
                setPrefWidth(125);
                setPrefHeight(OPpane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
                setOnlyAnimateWhenNotClicked(true);
            }};
            FusionButton lowButton = new FusionButton("压缩后") {{
                setPrefWidth(125);
                setPrefHeight(OPpane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
                setLayoutX(155);
                setOnlyAnimateWhenNotClicked(true);
            }};

            if (outputState==1)
            {
                highButton.setDisable(true);
                lowButton.setDisable(false);
            }else{
                highButton.setDisable(false);
                lowButton.setDisable(true);
            }

            highButton.setOnAction(event -> {
                if(outputState==0){
                    outputState=1;
                    highButton.setDisable(true);
                    lowButton.setDisable(false);
                }
            });

            lowButton.setOnAction(event -> {
                if(outputState==1){
                    outputState=0;
                    highButton.setDisable(false);
                    lowButton.setDisable(true);
                }
            });

            qualityPane.getContentPane().getChildren().add(highButton);
            qualityPane.getContentPane().getChildren().add(lowButton);

            stage.getInitialScene().getContentPane().getChildren().add(flowPaneRec);
            stage.getInitialScene().getContentPane().getChildren().add(scrollOutputFlowPane.getNode());
            stage.getInitialScene().getContentPane().getChildren().add(OPpane.getNode());
            stage.getInitialScene().getContentPane().getChildren().add(qualityPane.getNode());
            stage.show();
        });
        //以下为历史记录的部分
        VScrollPane scrollHisFlowPane = new VScrollPane() {{
            getNode().setLayoutX(10);
            getNode().setLayoutY(10);
            getNode().setPrefWidth(330);
            getNode().setPrefHeight(650);
        }};
        var hisFlowPane = new FlowPane() {{
            setLayoutX(0);
            setLayoutY(0);
            setPrefHeight(scrollHisFlowPane.getNode().getPrefHeight());
            setPrefWidth(scrollHisFlowPane.getNode().getPrefWidth());
            // 设置列间距
            setVgap(5);
        }};

        // 创建一个矩形用于显示flowPane的边框
        Rectangle hisFlowPaneRec = new Rectangle(scrollHisFlowPane.getNode().getLayoutX() - 5, scrollHisFlowPane.getNode().getLayoutY() - 5, hisFlowPane.getPrefWidth() + 5, hisFlowPane.getPrefHeight() + 5) {{
            setFill(Color.TRANSPARENT);
            setStroke(Color.WHITE); // 设置矩形的边框颜色
            setStrokeType(StrokeType.INSIDE);//边框为内嵌式，不会超出pane的范围
        }};

        //反序列化过程
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./src/main/resources/serializedData/testData.dat"))) {
            // 读取整个列表
            importHistories = (List<ImportHistory>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //开始添加按钮
        if(importHistories!=null){
            for(ImportHistory importHistory:importHistories){
                FusionButton hisButton = new FusionButton(importHistory.getDate()) {{
                    setPrefWidth(320);
                    setPrefHeight(50);
                    setOnlyAnimateWhenNotClicked(true);
                }};

                hisButton.setOnAction(e->{
                    int len=totalImages.size();
                    for(int i=0;i<len;i++)
                        totalImages.get(0).delete();
                });

                hisFlowPane.getChildren().add(hisButton);
            }
        }
        //绑定两个pane的宽和高
        FXUtils.observeWidthHeight(scrollHisFlowPane.getNode(), hisFlowPane);
        scrollHisFlowPane.setContent(hisFlowPane);
        getContentPane().getChildren().add(hisFlowPaneRec);
        getContentPane().getChildren().add(scrollHisFlowPane.getNode());

    }


    /***
     * @Description 创建多个FusionButtonBox 含有图片 和文字说明
     * @return java.util.List<Button>
     * @author 张喆宇
     * @date 2023/12/4 21:33
     **/
    private List<VBox> createImageButtonsVbox() {
        List<VBox> buttonBoxs = new ArrayList<>();

        if (selectedImages.isEmpty()) {
            return null;
        }

        for (ImageObj imageObj : selectedImages) {
            FusionImageButton button = new FusionImageButton();
            FusionImageButton copy = new FusionImageButton();
            // 设置按钮大小
            button.setPrefSize(80, 80);
            copy.setPrefSize(80, 80);
            // 添加按钮点击事件处理程序
            button.setOnAction(e -> {
                if (StaticValues.editingImageObj != imageObj) {
                    System.out.println("选择成功");
                    StaticValues.editingImageObj = imageObj;
                    Histogram.drawHistogram(StaticValues.editingImageObj.getEditingImage());
                    for(ImageObj imageObj1:totalImages){
                        if(imageObj1.getImageButton().isDisable())
                        {
                            imageObj1.getImageButton().setDisable(false);
                            imageObj1.getButtonVBox().setBackground(null);
                        }

                        if(imageObj1.getCopyButton().isDisable()){
                            imageObj1.getCopyButton().setDisable(false);
                            imageObj1.getCopyVBox().setBackground(null);
                        }

                    }
                    imageObj.getButtonVBox().setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5), null)));
                    imageObj.getCopyVBox().setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5), null)));
                    imageObj.getImageButton().setDisable(true);
                    imageObj.getCopyButton().setDisable(true);
                }
            });
            copy.setOnAction(e -> {
                if (StaticValues.editingImageObj != imageObj) {
                    System.out.println("选择成功");
                    StaticValues.editingImageObj = imageObj;
                    Histogram.drawHistogram(StaticValues.editingImageObj.getEditingImage());
                    for(ImageObj imageObj1:totalImages){
                        if(imageObj1.getImageButton().isDisable())
                        {
                            imageObj1.getImageButton().setDisable(false);
                            imageObj1.getButtonVBox().setBackground(null);
                        }

                        if(imageObj1.getCopyButton().isDisable()){
                            imageObj1.getCopyButton().setDisable(false);
                            imageObj1.getCopyVBox().setBackground(null);
                        }

                    }
                    imageObj.getButtonVBox().setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5), null)));
                    imageObj.getCopyVBox().setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5), null)));
                    imageObj.getImageButton().setDisable(true);
                    imageObj.getCopyButton().setDisable(true);
                    ImageEditScene.initEditImagePane();
                }
            });
            double selectedImageHeight = imageObj.getEditingImage().getHeight();
            double selectedImageWidth = imageObj.getEditingImage().getWidth();
            double rate = selectedImageHeight / selectedImageWidth;
            // 对图片较大压缩，用于生成图片按钮
            double buttonWidth = 80;
            double buttonHeight = 80;
            if (rate > 1) {
                buttonWidth = 80 / rate;
            } else {
                buttonHeight = 80 * rate;
            }
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageObj.getEditingImage(), null);
            BufferedImage compressedButtonBufferedImage = ConvertUtil.resetSize(bufferedImage, buttonWidth, buttonHeight, true);
            Image compressedButtonImage = ConvertUtil.ConvertToFxImage(compressedButtonBufferedImage);
            imageObj.setButtonImage(compressedButtonImage);
            button.getImageView().setImage(compressedButtonImage);
            button.getImageView().setLayoutX((80 - buttonWidth) / 2);
            button.getImageView().setLayoutY((80 - buttonHeight) / 2);
            copy.getImageView().setImage(compressedButtonImage);
            copy.getImageView().setLayoutX((80 - buttonWidth) / 2);
            copy.getImageView().setLayoutY((80 - buttonHeight) / 2);
            // 将按钮添加到列表 同时添加一个拷贝
            VBox copyVox = new VBox();
            copyVox.getChildren().add(copy);
            copyVox.getChildren().add(new HPadding(100));
            copyImageButtonsVbox.add(copyVox);
            imageObj.setImageButton(button);
            imageObj.setCopyButton(copy);
            imageObj.setCopyVBox(copyVox);
            Label descriptionLabel = new Label(Integer.toString((int) imageObj.getOriginalImage().getWidth()) + '×' + (int) imageObj.getOriginalImage().getHeight());
            descriptionLabel.setTextFill(Color.WHITE);
            VBox buttonVBox = new VBox();
            buttonVBox.setAlignment(Pos.CENTER); // 居中对齐
            buttonVBox.setSpacing(5);
            buttonVBox.getChildren().add(button);
            buttonVBox.getChildren().add(descriptionLabel);
            imageObj.setButtonVBox(buttonVBox);
            buttonBoxs.add(buttonVBox);
        }

        return buttonBoxs;
    }

    /***
     * @Description 清除所有产生的按钮box
     * @return null
     * @author 张喆宇
     * @date 2023/12/5 22:29
     **/

    public void clearImageButtonsVbox() {
        fusionImageButtonsVbox = null;
    }

    /***
     * @Description 返回所有选中的图片
     * @return java.util.List<org.example.Obj.ImageObj>
     * @author 张喆宇
     * @date 2023/12/9 13:38
     **/

    public List<ImageObj> getTotalImages() {
        return totalImages;
    }

    /***
     * @Description 传出单次选择产生的按钮Vbox
     * @return java.util.List<io.vproxy.vfx.ui.button.FusionImageButton>
     * @author 张喆宇
     * @date 2023/12/9 13:37
     **/

    public List<VBox> getFusionImageButtonsVbox() {
        return fusionImageButtonsVbox;
    }

    /***
     * @Description 传出单次选择产生的按钮
     * @return java.util.List<io.vproxy.vfx.ui.button.FusionButton>
     * @author 张喆宇
     * @date 2023/12/9 19:18
     **/

    public List<VBox> getCopyImageButtonsVboxButtons() {
        return copyImageButtonsVbox;
    }

    /***
     * @Description 清空单次选择产生的按钮
     * @author 张喆宇
     * @date 2023/12/9 19:19
     **/

    public void clearImageButtons() {
        copyImageButtonsVbox.clear();
    }

    @Override
    public String title() {
        return "ImportMenu";
    }
}
