package org.example.ImageTools;

import javafx.scene.image.Image;
import org.example.Main;

/**
 * 工具类，用来获取图像绝对路径并导入图像
 *
 * @author 吴鹄远
 * Date 2023/12/2 12:26
 */
public class ImportImageResource {
    private static final ImportImageResource instance = new ImportImageResource();

    public static ImportImageResource getInstance() {
        return instance;
    }

    public Image getImage(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        String resource = Main.class.getResource(path).toString();
        Image image = new Image(resource);
        return image;
    }
}
