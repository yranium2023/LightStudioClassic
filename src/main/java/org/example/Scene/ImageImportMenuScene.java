package org.example.Scene;

import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneRole;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 吴鹄远
 * @Description 这个类用于创建导入图像、查看导入历史的场景
 * @date 2023/12/4 14:37
 */
public class ImageImportMenuScene extends SuperScene{
    //所有所选中的图片
    private List<Image> selectedImages = new ArrayList<>();

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
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
            );

            // 显示文件选择器对话框并获取选中的多个文件
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                // 清空之前选中的图片
                selectedImages.clear();

                for (File selectedFile : selectedFiles) {
                    // 处理每个选中的图片文件，例如显示在界面上或传递给其他部分进行处理
                    String imagePath = selectedFile.toURI().toString();
                    Image selectedImage = new Image(imagePath);
                    // 将选中的图片添加到列表中
                    selectedImages.add(selectedImage);
                    // 在这里可以根据需要使用选择的图像做一些操作
                }
            }
        });



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



    @Override
    public String title() {
        return "ImportMenu";
    }
}
