package org.example.Obj;

import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.button.FusionImageButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.example.ImageStatistics.Histogram;
import org.example.ImageTools.ConvertUtil;
import org.example.StaticValues;

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
    //图库中按钮
    private FusionImageButton imageButton = null;
    //横板按钮
    private FusionImageButton  copyButton = null;
    //对比度滑动条，初始化为0.5
    private double contrastPercent=0.5;
    //曝光度滑动条，初始化为0.5
    private double exposurePercent=0.5;
    //饱和度滑动条，初始化为0.5
    private double saturationPercent=0.5;
    //色温滑动条，初始化为0.5
    private double temperaturePercent=0.5;

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

    /***
     * @Description  存入图库中按钮
     * @param imageButton
     * @author 张喆宇
     * @date 2023/12/10 0:30
    **/

    public void setImageButton(FusionImageButton  imageButton) {
        this.imageButton = imageButton;
    }

    /***
     * @Description  存入横版按钮
     * @param copyButton
     * @author 张喆宇
     * @date 2023/12/10 0:30
    **/

    public void setCopyButton(FusionImageButton  copyButton) {
        this.copyButton = copyButton;
    }

    /***
     * @Description 更新图片
     * @return null
     * @author 张喆宇
     * @date 2023/12/10 0:32
    **/
    private void renewButton(){
        this.imageButton.getImageView().setImage(buttonImage);
        this.imageButton.getImageView().setLayoutX((80 - buttonImage.getWidth()) / 2);
        this.imageButton.getImageView().setLayoutY((80 - buttonImage.getHeight()) / 2);
        this.copyButton.getImageView().setImage(buttonImage);
        this.copyButton.getImageView().setLayoutX((80 - buttonImage.getWidth()) / 2);
        this.copyButton.getImageView().setLayoutY((80 - buttonImage.getHeight()) / 2);
    }
    /**
     * @Description  这个类用来生成新的压缩图片、图标图片、直方图
     * @param nowImage
     * @author 吴鹄远
     * @date 2023/12/11 15:24
    **/

    public void renewAll(Image nowImage){
        //生成和替换缩略图
        Image newButtonImage=ImageObj.resizeButtonImage(nowImage);
        setButtonImage(newButtonImage);
        renewButton();
        //生成和替换压缩图片
        Image newEditingImage=ImageObj.resizeNormalImage(nowImage);
        setEditingImage(newEditingImage);
        Histogram.drawHistogram(newEditingImage);
    }

    public  double getContrastPercent() {
        return contrastPercent;
    }

    public void setContrastPercent(double contrastPercent) {
        this.contrastPercent = contrastPercent;
    }

    public double getExposurePercent() {
        return exposurePercent;
    }

    public void setExposurePercent(double exposurePercent) {
        this.exposurePercent = exposurePercent;
    }

    public double getSaturationPercent() {
        return saturationPercent;
    }

    public void setSaturationPercent(double saturationPercent) {
        this.saturationPercent = saturationPercent;
    }

    public double getTemperaturePercent() {
        return temperaturePercent;
    }

    public void setTemperaturePercent(double temperaturePercent) {
        this.temperaturePercent = temperaturePercent;
    }
}
