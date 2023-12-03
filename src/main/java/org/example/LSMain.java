package org.example;

import io.vproxy.vfx.control.globalscreen.GlobalScreenUtils;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.stage.VStage;
import javafx.application.Application;
import javafx.stage.Stage;
import org.example.ImageTools.ImportImageResource;
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

        mainScenes.add(new IntroScene());
        var initialScene = mainScenes.get(0);
        sceneGroup = new VSceneGroup(initialScene);
        for (var s : mainScenes) {
            if (s == initialScene) continue;
            sceneGroup.addScene(s);
        }

        stage.getStage().setWidth(1280);
        stage.getStage().setHeight(800);
        stage.getStage().centerOnScreen();
        stage.getStage().show();

    }
}
