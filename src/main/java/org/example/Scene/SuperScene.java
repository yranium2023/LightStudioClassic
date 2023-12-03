package org.example.Scene;

import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.scene.VSceneRole;

/**
 * @author 吴鹄远
 * @Description 所有Scene的超类
 * @date 2023/12/3 19:58
 */
public abstract class SuperScene extends VScene {
    public SuperScene(VSceneRole role) {
        super(role);
    }
    public abstract String title();
}
