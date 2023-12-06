package org.example.Scene;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneRole;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.stage.FileChooser;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.image.Image;

import org.example.StaticValues;

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
public class ImageImportMenuScene extends SuperScene{
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
        var IOpane=new FusionPane(){{
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

                            // 将选中的图片添加到列表中
                            selectedImages.add(selectedImage);

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
            return buttons;
        }

        for (Image image : selectedImages) {
            FusionImageButton button = new FusionImageButton();

            // 使用异步加载图片的服务
            ImageLoaderService imageLoaderService = new ImageLoaderService(image.getUrl());

            // 设置按钮大小
            button.setPrefSize(80, 80);
            button.getImageView().setFitWidth(80);
            button.getImageView().setFitHeight(80);

            // 添加按钮点击事件处理程序
            button.setOnAction(e -> {
                if (StaticValues.editingImage != image) {
                    System.out.println("选择成功");
                    StaticValues.editingImage = image;
                }
            });

            // 监听服务的成功事件，在成功加载图片后更新按钮的图像
            imageLoaderService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    Image loadedImage = imageLoaderService.getValue();
                    // 使用 Platform.runLater 来确保更新操作在 JavaFX Application 线程上执行
                    Platform.runLater(() -> button.getImageView().setImage(loadedImage));
                    button.getImageView().setLayoutY(10);
                }
            });

            // 启动异步加载任务
            imageLoaderService.start();

            // 将按钮添加到列表
            buttons.add(button);
        }

        return buttons;
    }

    /***
     * @Description  清除所有产生的按钮
     * @return null
     * @author 张喆宇
     * @date 2023/12/5 22:29
    **/

    public void clearImageButtons(){
        fusionImageButtons=null;
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
