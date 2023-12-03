package org.example.Scene;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.shapes.BrokenLine;
import io.vproxy.vfx.ui.shapes.EndpointStyle;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.ui.stage.VStageInitParams;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * @Description: 用于文件管理 暂时用于测试
 * @author 张喆宇
 * @date 2023/12/3 20:56
 */
public class ImageImportScene extends SuperScene{

    public ImageImportScene(VSceneRole role) {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();


        FusionButton defaultButton = new FusionButton("导入图片") {{
            setPrefWidth(320);
            setPrefHeight(150);
        }};

        defaultButton.setOnAction(e -> {
            // 创建文件选择器
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择图片");

            // 添加文件过滤器，限定选择的文件类型为图片
            FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                    "图片文件", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg");
            fileChooser.getExtensionFilters().add(imageFilter);

            // 显示文件选择器对话框并获取选中的文件
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                // 如果用户选择了文件，则加载该文件并显示在界面上
                String imagePath = selectedFile.toURI().toString();
                Image selectedImage = new Image(imagePath);

                // 这里你可以根据需要使用选择的图像做一些操作
                // 例如，显示在界面上或者传递给其他部分进行处理
            }
        });
    }



    @Override
    public String title() {
        return "ImageImport";
    }
}
