package org.example.Scene;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneRole;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.stage.FileChooser;
import org.example.ImageTools.ConvertUtil;
import org.example.StaticValues;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 吴鹄远
 * @Description 这个类用于创建导入图像、查看导入历史的场景
 * @date 2023/12/4 14:37
 */
public class ImageImportMenuScene extends SuperScene {
    //所有所选中的图片
    private List<Image> selectedImages = new ArrayList<>();

    private List<FusionImageButton> fusionImageButtons = null;

    public ImageImportMenuScene() {
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
                // 使用线程池处理文件导入
                ExecutorService executorService = Executors.newFixedThreadPool(5);
                CountDownLatch latch = new CountDownLatch(selectedFiles.size());

                for (File selectedFile : selectedFiles) {
                    executorService.submit(() -> {
                        try {
                            // 处理每个选中的图片文件，例如显示在界面上或传递给其他部分进行处理
                            String imagePath = selectedFile.toURI().toString();
                            Image selectedImage = new Image(imagePath);
                            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(selectedImage, null);
                            double selectedImageHeight = selectedImage.getHeight();
                            double selectedImageWidth = selectedImage.getWidth();
                            if (selectedImageHeight > 2000 || selectedImageWidth > 2000) {
                                double rate = selectedImageHeight / selectedImageWidth;
                                // 对图片稍微压缩，用于编辑
                                double editorHeight = 2000;
                                double editorWidth = 2000;
                                if (rate > 1) {
                                    editorWidth = 2000 / rate;
                                } else {
                                    editorHeight = 2000 * rate;
                                }
                                BufferedImage compressedEditorBufferedImage = ConvertUtil.resetSize(bufferedImage, editorWidth, editorHeight, true);
                                Image compressedEditorImage = ConvertUtil.ConvertToFxImage(compressedEditorBufferedImage);
                                // 将选中的图片添加到列表中
                                selectedImages.add(compressedEditorImage);
                            } else {
                                selectedImages.add(selectedImage);
                            }
                            System.out.println("传入一张图片成功");
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                // 等待所有任务完成
                try {
                    latch.await();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                executorService.shutdown();

                // 在 JavaFX 线程上更新 UI
                Platform.runLater(() -> {
                    // 生成特殊的按钮
                    fusionImageButtons = createImageButtons();
                    // 清空之前选中的图片
                    selectedImages.clear();
                    System.out.println("图片全部传入成功");
                });
            }
        });


    }

    /***
     * @Description 用于创建图片导入的任务 特殊类
     * @author 张喆宇
     * @date 2023/12/6 15:30
     **/

    class ImageLoaderService extends Service<Image> {
        private String imagePath;

        public ImageLoaderService(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        protected Task<Image> createTask() {
            return new Task<>() {
                @Override
                protected Image call() throws Exception {
                    return new Image(imagePath);
                }
            };
        }
    }

    /***
     * @Description 创建多个FusionButton 含有图片
     * @return java.util.List<Button>
     * @author 张喆宇
     * @date 2023/12/4 21:33
     **/
    private List<FusionImageButton> createImageButtons() {
        List<FusionImageButton> buttons = new ArrayList<>();

        if (selectedImages.isEmpty()) {
            return null;
        }

        for (Image image : selectedImages) {
            FusionImageButton button = new FusionImageButton();
            // 设置按钮大小
            button.setPrefSize(80, 80);
            // 添加按钮点击事件处理程序
            button.setOnAction(e -> {
                if (StaticValues.editingImage != image) {
                    System.out.println("选择成功");
                    StaticValues.editingImage = image;
                }
            });
            double selectedImageHeight = image.getHeight();
            double selectedImageWidth = image.getWidth();
            double rate = selectedImageHeight / selectedImageWidth;
            // 对图片较大压缩，用于生成图片按钮
            double buttonWidth = 80;
            double buttonHeight = 80;
            if (rate > 1) {
                buttonWidth = 80 / rate;
            } else {
                buttonHeight = 80 * rate;
            }

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            BufferedImage compressedButtonBufferedImage = ConvertUtil.resetSize(bufferedImage, buttonWidth, buttonHeight, true);
            Image compressedButtonImage = ConvertUtil.ConvertToFxImage(compressedButtonBufferedImage);
            button.getImageView().setImage(compressedButtonImage);
            button.getImageView().setLayoutX((80-buttonWidth)/2);
            button.getImageView().setLayoutY((80-buttonHeight)/2);
            // 将按钮添加到列表
            buttons.add(button);
        }

        return buttons;
    }

    /***
     * @Description 清除所有产生的按钮
     * @return null
     * @author 张喆宇
     * @date 2023/12/5 22:29
     **/

    public void clearImageButtons() {
        fusionImageButtons = null;
    }

    /***
     * @Description 返回所有选中的图片
     * @return java.util.List<javafx.scene.image.Image>
     * @author 张喆宇
     * @date 2023/12/4 18:53
     **/
    public List<Image> getSelectedImages() {
        return selectedImages;
    }


    public List<FusionImageButton> getFusionImageButtons() {
        return fusionImageButtons;
    }

    @Override
    public String title() {
        return "ImportMenu";
    }
}
