package org.example.Obj;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.example.ImageTools.ConvertUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张喆宇
 * @Description: 统一管理每个导入的图片 有其原图 精致压缩（用于编辑）和粗糙压缩（用于当图标使用）的图片
 * @date 2023/12/9 11:05
 */
public class ImageObj {
    //传入的原始图片
    private Image originalImage = null;
    //压缩到80*80的小图片
    private Image buttonImage = null;
    //压缩到2k的大图片
    private Image editingImage =null;
    //裁减过程中产生的图片列表
    private List<Image> clipImages = new ArrayList<>();
    //编辑过程中产生的图片列表
    private List<Image> editImages = new ArrayList<>();
    //传入图片的路径
    String imagePath = null;

    /***
     * @Description  构造函数 用于构建Image对象
     * @param originalImage
     * @return null
     * @author 张喆宇
     * @date 2023/12/9 11:09
    **/

    public ImageObj(Image originalImage) {
        this.originalImage = originalImage;
        this.imagePath=originalImage.getUrl();
    }
    /***
     * @Description  传入按钮图片
     * @param buttonImage
     * @author 张喆宇
     * @date 2023/12/9 11:13
    **/

    public void setButtonImage(Image buttonImage) {
        this.buttonImage = buttonImage;
    }

    /***
     * @Description  传入编辑用图片
     * @param editingImage
     * @author 张喆宇
     * @date 2023/12/9 11:13
    **/

    public void setEditingImage(Image editingImage) {
        this.editingImage = editingImage;
    }

    /***
     * @Description  获取原图
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 11:14
    **/

    public Image getOriginalImage() {
        return originalImage;
    }

    /***
     * @Description  获取按钮图片
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 11:14
    **/

    public Image getButtonImage() {
        return buttonImage;
    }

    /***
     * @Description  获取编辑中图片
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 11:14
    **/

    public Image getEditingImage() {
        return editingImage;
    }

    /***
     * @Description  获取图片路径
     * @return java.lang.String
     * @author 张喆宇
     * @date 2023/12/9 11:14
    **/

    public String getImagePath() {
        return imagePath;
    }

    /***
     * @Description  获取裁剪图片列表
     * @author 张喆宇
     * @date 2023/12/9 11:26
    **/

    public List<Image> getClipImages() {
        return clipImages;
    }

    /***
     * @Description  获取编辑图片列表
     * @author 张喆宇
     * @date 2023/12/9 11:28
    **/
    public List<Image> getEditImages() {
        return editImages;
    }

    /***
     * @Description  用于压缩图片 普通压缩
     * @param image
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 21:47
    **/

    public static Image resizeNormalImage(Image image){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        double imageHeight = image.getHeight();
        double imageWidth = image.getWidth();
        if (imageHeight > 2000 || imageWidth > 2000) {
            double rate = imageHeight / imageWidth;
            // 对图片稍微压缩，用于编辑
            double editorHeight = 2000;
            double editorWidth = 2000;
            if (rate > 1) {
                editorWidth = 2000 / rate;
            } else {
                editorHeight = 2000 * rate;
            }
            BufferedImage compressedEditorBufferedImage = ConvertUtil.resetSize(bufferedImage, editorWidth, editorHeight, true);
            return ConvertUtil.ConvertToFxImage(compressedEditorBufferedImage);
        } else {
            return image;
        }
    }

    /***
     * @Description  用于压缩图片 按钮级别压缩
     * @param image
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 21:49
    **/

    public static Image resizeButtonImage(Image image){
        double imageHeight = image.getHeight();
        double imageWidth = image.getWidth();
        double rate = imageHeight / imageWidth;
        // 对图片较大压缩，用于生成图片按钮
        double buttonWidth = 80;
        double buttonHeight = 80;
        if (rate > 1) {
            buttonWidth = 80 / rate;
        } else {
            buttonHeight = 80 * rate;
        }
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage compressedButtonBufferedImage = ConvertUtil.resetSize(bufferedImage, buttonWidth, buttonHeight, true);
        return ConvertUtil.ConvertToFxImage(compressedButtonBufferedImage);
    }

}
