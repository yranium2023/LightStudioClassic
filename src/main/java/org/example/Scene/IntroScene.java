package org.example.Scene;

import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.scene.VSceneRole;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * @author 吴鹄远
 * @Description 这是一个Intro场景
 * @date 2023/12/3 19:59
 */
public class IntroScene extends SuperScene {
    public IntroScene(){
        super(VSceneRole.MAIN);
        enableAutoContentWidthHeight();
        var label = new ThemeLabel("Welcome to LSC") {{
            FontManager.get().setFont(this, settings -> settings.setSize(40));
        }};

        getContentPane().getChildren().add(label);
        FXUtils.observeWidthHeightCenter(getContentPane(),label);
    }

    @Override
    public String title() {
        return "Intro";
    }
}
