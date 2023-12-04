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
//        var pane = new VBox(
//                new ThemeLabel("Contributors:       ") {{
//                    FontManager.get().setFont(this, settings -> settings.setSize(25));
//                }},
//                new VPadding(20),
//                new ThemeLabel("yranium\nmissing\nAlbert_Ling")
//        ) {{
//            setAlignment(Pos.BOTTOM_RIGHT);
//        }};
//
//        StackPane root = new StackPane();
//        getContentPane().getChildren().add(root);
//        root.getChildren().add(pane);
//        StackPane.setAlignment(pane,Pos.BOTTOM_RIGHT);
//        FXUtils.observeHeight(getContentPane(),root,-10);
//        FXUtils.observeWidth(getContentPane(),root,-100);





    }

    @Override
    public String title() {
        return "Intro";
    }
}
