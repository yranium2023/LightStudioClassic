package org.example.Scene;

import io.vproxy.vfx.ui.scene.VSceneRole;

/**
 * @author 吴鹄远
 * @Description
 * @date 2023/12/4 17:00
 */
public class ImageClipScene extends SuperScene{
    public ImageClipScene() {
        super(VSceneRole.DRAWER_VERTICAL);
    }

    @Override
    public String title() {
        return "ImageClip";
    }
}
