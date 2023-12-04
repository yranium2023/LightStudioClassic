package org.example;

import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.HPadding;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneHideMethod;
import io.vproxy.vfx.ui.scene.VSceneShowMethod;
import io.vproxy.vfx.ui.stage.VStage;
import io.vproxy.vfx.util.FXUtils;
import javafx.application.Application;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ImageTools.ImportImageResource;
import org.example.Scene.ImageImportScene;
import org.example.Scene.IntroScene;
import org.example.Scene.SuperScene;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 吴鹄远
 * @Description 这是承载Application的场景
 * @date 2023/12/3 20:07
 */
public class LSMain extends Application {
    //承载所有场景
    private final List<SuperScene> mainScenes = new ArrayList<>();
    private VSceneGroup sceneGroup;

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.getIcons().add(ImportImageResource.getInstance().getImage("image/icon.png"));
        var stage = new VStage(primaryStage) {
            @Override
            public void close() {
                super.close();
                TaskManager.get().terminate();
                GlobalScreenUtils.unregister();
            }
        };
        stage.getInitialScene().enableAutoContentWidthHeight();
        stage.setTitle("LightStudioClassic v1.0");

        //添加scene 要用的记得添加进去
        mainScenes.add(new IntroScene());
        mainScenes.add(new ImageImportScene(()->sceneGroup));


        var initialScene = mainScenes.get(0);
        sceneGroup = new VSceneGroup(initialScene);
        for (var s : mainScenes) {
            if (s == initialScene) continue;
            sceneGroup.addScene(s);
        }

        var navigatePane = new FusionPane() {{
            getNode().setPrefWidth(250);
            getNode().setPrefHeight(50);
            getNode().setLayoutX(1020);
            getNode().setLayoutY(15);
        }};


        FXUtils.observeHeight(stage.getInitialScene().getContentPane(), sceneGroup.getNode(), -80);

        FXUtils.observeWidthHeight(stage.getInitialScene().getContentPane(), sceneGroup.getNode());


        //以下部分为测试所用，增加一个前往ImageImportScene的按钮
        var imageImportScene = mainScenes.get(1);

        var InputButton = new FusionButton("图库") {{
            setPrefWidth(100);
            setPrefHeight(navigatePane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
            setOnlyAnimateWhenNotClicked(true);
        }};
        var ImageEditButton = new FusionButton("编辑") {{
            setPrefWidth(100);
            setPrefHeight(navigatePane.getNode().getPrefHeight() - FusionPane.PADDING_V * 2);
            setOnlyAnimateWhenNotClicked(true);
        }};


        navigatePane.getContentPane().widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            var v = now.doubleValue();
            InputButton.setLayoutX(v - 125 - InputButton.getPrefWidth());
            ImageEditButton.setLayoutX(v - ImageEditButton.getPrefWidth()-5);
        });
        stage.getInitialScene().getContentPane().widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            var v = now.doubleValue();

            navigatePane.getNode().setLayoutX(v-270);

        });
        InputButton.setOnAction(e -> {
            sceneGroup.show(imageImportScene, VSceneShowMethod.FROM_LEFT);
        });

        navigatePane.getContentPane().getChildren().add(InputButton);
        navigatePane.getContentPane().getChildren().add(ImageEditButton);


        stage.getInitialScene().getContentPane().getChildren().add(sceneGroup.getNode());
        stage.getInitialScene().getContentPane().getChildren().add(navigatePane.getNode());


        stage.getStage().setWidth(1280);
        stage.getStage().setHeight(800);
        stage.getStage().centerOnScreen();
        stage.getStage().show();

    }
}
