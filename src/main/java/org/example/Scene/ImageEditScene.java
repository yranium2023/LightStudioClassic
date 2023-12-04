package org.example.Scene;

import io.vproxy.vfx.ui.scene.VSceneGroup;
import io.vproxy.vfx.ui.scene.VSceneRole;

import java.util.function.Supplier;

/**
 * @author 吴鹄远
 * @Description 该场景用于调整图像的基础参数
 * @date 2023/12/3 21:07
 */
public class ImageEditScene extends SuperScene{
    public ImageEditScene(Supplier<VSceneGroup> sceneGroupSup) {
        super(VSceneRole.MAIN);

        
    }

    @Override
    public String title() {
        return "ImageEdit";
    }
}
