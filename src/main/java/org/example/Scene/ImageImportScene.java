package org.example.Scene;

import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.scene.*;
import io.vproxy.vfx.ui.stage.VStage;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import org.example.ImageTools.ImportImageResource;

import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javafx.scene.paint.Color;
/**
 * @Description: 用于文件管理 暂时用于测试
 * @author 张喆宇
 * @date 2023/12/3 20:56
 */
public class ImageImportScene extends SuperScene{

    private Image editingImage = null;

    public ImageImportMenuScene menuScene=new ImageImportMenuScene();
    public ImageImportScene(Supplier<VSceneGroup> sceneGroupSup) {
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();
        //创建左上角的menuButton
        var menuBtn = new FusionImageButton(ImportImageResource.getInstance().getImage("image/menu.png")) {{
            setPrefWidth(40);
            setPrefHeight(VStage.TITLE_BAR_HEIGHT + 1);
            getImageView().setFitHeight(15);
            setLayoutX(-2);
            setLayoutY(-1);
        }};
        // 创建 FlowPane 用于放图片按钮
        FlowPane flowPane = new FlowPane();
        flowPane.setLayoutX(100);
        flowPane.setLayoutY(100);
        flowPane.setPrefWidth(900);
        flowPane.setPrefHeight(550);
        // 设置行列间距
        flowPane.setHgap(50);
        flowPane.setVgap(25);
        // 创建一个矩形用于显示pane
        Rectangle rectangle = new Rectangle(flowPane.getLayoutX()-20,flowPane.getLayoutY()-20,flowPane.getPrefWidth(),flowPane.getPrefHeight());
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.WHITE); // 设置矩形的边框颜色
        menuScene.enableAutoContentWidthHeight();
        menuBtn.setOnAction(e->{
            if(!sceneGroupSup.get().getScenes().contains(menuScene)){
                sceneGroupSup.get().addScene(menuScene, VSceneHideMethod.TO_LEFT);
            }
            sceneGroupSup.get().show(menuScene, VSceneShowMethod.FROM_LEFT);
            //如果没有选择要编辑的图片 直接进入编辑图片的话 默认选择第一张图片
            if(!menuScene.getSelectedImages().isEmpty()){
                System.out.println("选择成功");
                editingImage =menuScene.getSelectedImages().get(0);
            }
            if(!menuScene.getSelectedImages().isEmpty()){
                // 添加多个图片按钮
                List<FusionImageButton> imageButtons = createImageButtons();
                // 将按钮添加到 FlowPane
                flowPane.getChildren().addAll(imageButtons);
            }
            menuScene.getSelectedImages().clear();

        });
        getContentPane().getChildren().add(rectangle);
        getContentPane().getChildren().add(menuBtn);
        getContentPane().getChildren().add(flowPane);
    }

    /***
     * @Description  创建多个FusionButton 含有图片
     * @return java.util.List<Button>
     * @author 张喆宇
     * @date 2023/12/4 21:33
     **/
    private List<FusionImageButton> createImageButtons() {
        List<FusionImageButton> buttons = new ArrayList<>();

        for (Image image : menuScene.getSelectedImages()) {
            // 创建按钮
            FusionImageButton button = new FusionImageButton(image);

            // 设置按钮大小
            button.setPrefSize(80, 80);
            button.getImageView().setFitWidth(80);
            button.getImageView().setFitHeight(80);
            // 添加按钮点击事件处理程序
            button.setOnAction(e->{
                if(editingImage !=image){
                    System.out.println("选择成功");
                    editingImage =image;
                }
            });
            // 将按钮添加到列表
            buttons.add(button);
        }

        return buttons;
    }

    /***
     * @Description  返回所需要编辑的图片
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/4 18:59
    **/
    public Image getEditingImage() {
        return editingImage;
    }

    @Override
    public String title() {
        return "ImageImport";
    }

}
