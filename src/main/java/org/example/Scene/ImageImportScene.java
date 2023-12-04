package org.example.Scene;

import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import io.vproxy.vfx.ui.scene.*;
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
import org.example.ImageTools.ImportImageResource;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @Description: 用于文件管理 暂时用于测试
 * @author 张喆宇
 * @date 2023/12/3 20:56
 */
public class ImageImportScene extends SuperScene{

    private Image editingImages = null;

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

        menuScene.enableAutoContentWidthHeight();
        menuBtn.setOnAction(e->{
            if(!sceneGroupSup.get().getScenes().contains(menuScene)){
                sceneGroupSup.get().addScene(menuScene, VSceneHideMethod.TO_LEFT);
            }
            sceneGroupSup.get().show(menuScene, VSceneShowMethod.FROM_LEFT);
        });
        getContentPane().getChildren().add(menuBtn);

        //如果没有选择要编辑的图片 直接进入编辑图片的话 默认选择第一张图片
        if(!menuScene.getSelectedImages().isEmpty()){
            editingImages=menuScene.getSelectedImages().get(0);
        }

    }

    /***
     * @Description  返回所需要编辑的图片
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/4 18:59
    **/

    public Image getEditingImages() {
        return editingImages;
    }

    @Override
    public String title() {
        return "ImageImport";
    }

}
