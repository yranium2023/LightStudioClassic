package org.example.Scene;

import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;

/**
 * 所有Scene的超类
 * @author 吴鹄远
 * Date 2023/12/3 19:58
 */
public abstract class SuperScene extends VScene {

    public SuperScene(VSceneRole role) {
        super(role);
    }

    public abstract String title();
}
