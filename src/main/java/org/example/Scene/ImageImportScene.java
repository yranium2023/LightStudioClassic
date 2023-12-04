package org.example.Scene;

import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.shapes.BrokenLine;
import io.vproxy.vfx.ui.shapes.EndpointStyle;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

/**
 * @Description: 用于文件管理 暂时用于测试
 * @author 张喆宇
 * @date 2023/12/3 20:56
 */
public class ImageImportScene extends SuperScene{

    public ImageImportScene() {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();

        var menuScene = new VScene(VSceneRole.DRAWER_VERTICAL);
        menuScene.getNode().setPrefWidth(450);
        menuScene.enableAutoContentWidth();
        menuScene.getNode().setBackground(new Background(new BackgroundFill(
                Theme.current().subSceneBackgroundColor(),
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));


        var menuBtn = new FusionImageButton(ImageManager.get().load("image/menu.png")) {{
            setPrefWidth(40);
            setPrefHeight(VStage.TITLE_BAR_HEIGHT + 1);
            getImageView().setFitHeight(15);
            setLayoutX(-2);
            setLayoutY(-1);
        }};
        FusionButton defaultButton = new FusionButton("导入图片") {{
            setPrefWidth(320);
            setPrefHeight(150);
        }};

        defaultButton.setOnAction(e -> {
            // 创建文件选择器
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择图片");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); // 设置初始目录

            // 添加文件过滤器，限定选择的文件类型为图片
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
            );

            // 显示文件选择器对话框并获取选中的多个文件
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                for (File selectedFile : selectedFiles) {
                    // 处理每个选中的图片文件，例如显示在界面上或传递给其他部分进行处理
                    String imagePath = selectedFile.toURI().toString();
                    Image selectedImage = new Image(imagePath);
                    // 在这里可以根据需要使用选择的图像做一些操作
                }
            }

        });

        var gridPane = new GridPane();
        gridPane.setLayoutY(300);
        gridPane.setHgap(50);
        gridPane.setVgap(50);
        FXUtils.observeWidthCenter(getContentPane(), gridPane);
        gridPane.add(defaultButton, 0, 0);

        getContentPane().getChildren().addAll(
                gridPane
        );
    }

    @Override
    public String title() {
        return "ImageImport";
    }
}
