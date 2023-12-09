package org.example.Obj;

import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 张喆宇
 * @Description: 统一管理每个导入的图片 有其原图 精致压缩（用于编辑）和粗糙压缩（用于当图标使用）的图片
 * @date 2023/12/9 11:05
 */
public class ImageObj {
    private Image originalImage = null;//传入的原始图片
    private Image buttonImage = null;//压缩到80*80的小图片
    private Image editingImage =null;//压缩到2k的大图片

    private List<Image> clipImages = new ArrayList<>();

    private List<Image> editImages = new ArrayList<>();

    String imagePath = null;//传入图片的路径

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
     * @Description  向裁剪列表中添加一张裁剪后的图片
     * @param image
     * @author 张喆宇
     * @date 2023/12/9 11:26
    **/

    public void addClipImage(Image image){
        this.clipImages.add(image);
    }

    /***
     * @Description  向编辑猎豹中添加一张编辑后的图片
     * @param image
     * @author 张喆宇
     * @date 2023/12/9 11:28
    **/

    public void addEditImage(Image image){
        this.editImages.add(image);
    }

}
