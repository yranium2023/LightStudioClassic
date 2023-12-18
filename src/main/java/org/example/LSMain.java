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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ImageModification.AutoWhiteBalance;
import org.example.ImageStatistics.Histogram;
import org.example.ImageTools.ImportImageResource;
import org.example.Obj.ImageObj;
import org.example.Obj.ImportHistory;
import org.example.Scene.*;

import java.io.*;
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
    private static VStage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.getIcons().add(ImportImageResource.getInstance().getImage("image/icon.png"));
        stage = new VStage(primaryStage) {
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
        mainScenes.add(new ImageImportScene(() -> sceneGroup));
        mainScenes.add(new ImageEditScene(() -> sceneGroup));


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
        ImageImportScene imageImportScene = (ImageImportScene) mainScenes.get(1);
        ImageEditScene imageEditScene = (ImageEditScene) mainScenes.get(2);

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
            ImageEditButton.setLayoutX(v - ImageEditButton.getPrefWidth() - 5);
        });

        stage.getInitialScene().getContentPane().widthProperty().addListener((ob, old, now) -> {
            if (now == null) return;
            var v = now.doubleValue();
            navigatePane.getNode().setLayoutX(v - 270);

        });
        InputButton.setOnAction(e -> {
            StaticValues.importHistogramPane(imageImportScene.histogramPane);
            sceneGroup.show(imageImportScene, VSceneShowMethod.FROM_LEFT);
        });
        ImageEditButton.setOnAction(e -> {
            StaticValues.importHistogramPane(imageEditScene.histogramPane);
            imageEditScene.initImageEditScene();
            sceneGroup.show(imageEditScene, VSceneShowMethod.FROM_RIGHT);
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

    @Override
    public void stop() throws Exception {
        if(!ImageImportMenuScene.totalImages.isEmpty())
            ImageImportMenuScene.importHistories.add(new ImportHistory(ImageImportMenuScene.totalImages));
        File tmpFile = new File("./src/main/resources/serializedData/testData.dat");
        // 获取文件的父目录
        File parentDirectory = tmpFile.getParentFile();
        // 如果父目录不存在，创建它和所有不存在的父目录
        if (!parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }
        if(!tmpFile.exists()){
            try {
                tmpFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(!ImageImportMenuScene.importHistories.isEmpty()){
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./src/main/resources/serializedData/testData.dat"))) {
                // 写入整个列表
                oos.writeObject(ImageImportMenuScene.importHistories);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("序列化结束");
        }
        else{
            System.out.println("无需序列化");
        }
        super.stop();
    }

    public static VStage getStage() {
        return stage;
    }
}
