package org.example.Obj;

import io.vproxy.vfx.ui.button.FusionImageButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.Curve.SplineCanvas.SplineCanvas;
import org.example.ImageStatistics.Histogram;
import org.example.ImageTools.ConvertUtil;
import org.example.Scene.ImageEditScene;
import org.example.Scene.ImageImportMenuScene;
import org.example.Scene.ImageImportScene;
import org.example.StaticValues;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    private Image editingImage = null;
    //裁减过程中产生的图片列表
    private List<Image> clipImages = new ArrayList<>();
    //传入图片的路径
    String imagePath = null;
    //图片的名称
    String imageName = null;
    //图库中整个vbox
    private VBox buttonVBox = null;
    //图库中按钮
    private FusionImageButton imageButton = null;
    //横板按钮
    private FusionImageButton copyButton = null;
    //横版中整个vbox
    private VBox copyVBox = null;
    //导出图片中整个vbox
    private VBox outPutImageVBox = null;
    //对比度滑动条，初始化为0.5
    private double contrastPercent = 0.5;
    //曝光度滑动条，初始化为0.5
    private double exposurePercent = 0.5;
    //饱和度滑动条，初始化为0.5
    private double saturationPercent = 0.5;
    //色温滑动条，初始化为0.5
    private double temperaturePercent = 0.5;
    //创建一个枚举类型，存储当前滑动条是四个滑动条中的哪一条
    private sliderType_1 nowSlider_1 = null;

    public enum sliderType_1 {
        CONTRAST,
        EXPOSURE,
        SATURATION,
        TEMPERATURE
    }

    //创建一个属于自己的曲线，这样就不用记录了
    private SplineCanvas splineCanvas = new SplineCanvas(190);


    private HashMap<HSLColor,HSLInfo> hslInfos = new HashMap<>() {{
        put(HSLColor.Red,new HSLInfo(HSLColor.Red));
        put(HSLColor.Yellow,new HSLInfo(HSLColor.Yellow));
        put(HSLColor.Orange,new HSLInfo(HSLColor.Orange));
        put(HSLColor.Green,new HSLInfo(HSLColor.Green));
        put(HSLColor.Cyan,new HSLInfo(HSLColor.Cyan));
        put(HSLColor.Blue,new HSLInfo(HSLColor.Blue));
        put(HSLColor.Purple,new HSLInfo(HSLColor.Purple));
    }};

    /***
     * @Description 构造函数 用于构建Image对象
     * @param originalImage
     * @return null
     * @author 张喆宇
     * @date 2023/12/9 11:09
     **/

    public ImageObj(Image originalImage) {
        this.originalImage = originalImage;
        this.imagePath = originalImage.getUrl();
    }

    /***
     * @Description 传入按钮图片
     * @param buttonImage
     * @author 张喆宇
     * @date 2023/12/9 11:13
     **/

    public void setButtonImage(Image buttonImage) {
        this.buttonImage = buttonImage;
    }

    /***
     * @Description 传入编辑用图片
     * @param editingImage
     * @author 张喆宇
     * @date 2023/12/9 11:13
     **/

    public void setEditingImage(Image editingImage) {
        this.editingImage = editingImage;
    }

    /***
     * @Description 获取原图
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 11:14
     **/

    public Image getOriginalImage() {
        return originalImage;
    }

    /***
     * @Description 获取按钮图片
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 11:14
     **/

    public Image getButtonImage() {
        return buttonImage;
    }

    /***
     * @Description 获取编辑中图片
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 11:14
     **/

    public Image getEditingImage() {
        return editingImage;
    }

    /***
     * @Description 获取图片路径
     * @return java.lang.String
     * @author 张喆宇
     * @date 2023/12/9 11:14
     **/

    public String getImagePath() {
        return imagePath;
    }

    /***
     * @Description 获取裁剪图片列表
     * @author 张喆宇
     * @date 2023/12/9 11:26
     **/

    public List<Image> getClipImages() {
        return clipImages;
    }

    /***
     * @Description 用于压缩图片 普通压缩
     * @param image
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 21:47
     **/

    public static Image resizeNormalImage(Image image) {
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
     * @Description 用于压缩图片 按钮级别压缩
     * @param image
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * @date 2023/12/9 21:49
     **/

    public static Image resizeButtonImage(Image image) {
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
     * @Description 存入图库中按钮
     * @param imageButton
     * @author 张喆宇
     * @date 2023/12/10 0:30
     **/

    public void setImageButton(FusionImageButton imageButton) {
        this.imageButton = imageButton;
    }

    public void setButtonVBox(VBox buttonVBox) {
        this.buttonVBox = buttonVBox;
    }

    public void setCopyVBox(VBox copyVBox) {
        this.copyVBox = copyVBox;
    }

    /***
     * @Description 存入横版按钮
     * @param copyButton
     * @author 张喆宇
     * @date 2023/12/10 0:30
     **/

    public void setCopyButton(FusionImageButton copyButton) {
        this.copyButton = copyButton;
    }

    /***
     * @Description 更新图片
     * @return null
     * @author 张喆宇
     * @date 2023/12/10 0:32
     **/
    private void renewButton() {
        this.imageButton.getImageView().setImage(buttonImage);
        this.imageButton.getImageView().setLayoutX((80 - buttonImage.getWidth()) / 2);
        this.imageButton.getImageView().setLayoutY((80 - buttonImage.getHeight()) / 2);
        this.copyButton.getImageView().setImage(buttonImage);
        this.copyButton.getImageView().setLayoutX((80 - buttonImage.getWidth()) / 2);
        this.copyButton.getImageView().setLayoutY((80 - buttonImage.getHeight()) / 2);
    }

    /**
     * @param nowImage
     * @Description 这个类用来生成新的压缩图片、图标图片、直方图、和图片面熟
     * @author 吴鹄远
     * @date 2023/12/11 15:24
     **/

    public void renewAll(Image nowImage) {
        //生成和替换缩略图
        Image newButtonImage = ImageObj.resizeButtonImage(nowImage);
        setButtonImage(newButtonImage);
        renewButton();
        //生成和替换压缩图片
        Image newEditingImage = ImageObj.resizeNormalImage(nowImage);
        setEditingImage(newEditingImage);
        Histogram.drawHistogram(newEditingImage);
        this.buttonVBox.getChildren().remove(1);
        Label descriptionLabel = new Label(Integer.toString((int) newEditingImage.getWidth()) + '×' + (int) newEditingImage.getHeight());
        descriptionLabel.setTextFill(Color.WHITE);
        this.buttonVBox.getChildren().add(descriptionLabel);
    }

    /***
     * @Description 用于从两个图片按钮以及总图片中移除所有产生的按钮
     * @author 张喆宇
     * @date 2023/12/12 22:17
     **/

    public void delete() {
        ImageImportMenuScene.totalImages.remove(this);
        ImageImportScene.flowImportPane.getChildren().remove(this.buttonVBox);
        ImageEditScene.hEditBox.getChildren().remove(this.copyVBox);
        ImageImportScene.menuScene.getCopyImageButtonsVboxButtons().remove(this.copyVBox);
        if (!ImageImportMenuScene.totalImages.isEmpty())
            StaticValues.editingImageObj = ImageImportMenuScene.totalImages.get(0);
        Histogram.drawHistogram(StaticValues.editingImageObj.getEditingImage());
        ImageEditScene.initEditImagePane();
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public double getContrastPercent() {
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

    public sliderType_1 getNowSlider_1() {
        return nowSlider_1;
    }

    public void setNowSlider_1(sliderType_1 nowSlider_1) {
        this.nowSlider_1 = nowSlider_1;
    }

    public FusionImageButton getImageButton() {
        return imageButton;
    }

    public FusionImageButton getCopyButton() {
        return copyButton;
    }

    public VBox getButtonVBox() {
        return buttonVBox;
    }

    public VBox getCopyVBox() {
        return copyVBox;
    }

    public VBox getOutPutImageVBox() {
        return outPutImageVBox;
    }

    public void setOutPutImageVBox(VBox outPutImageVBox) {
        this.outPutImageVBox = outPutImageVBox;
    }

    public SplineCanvas getSplineCanvas() {
        return splineCanvas;
    }

    public HashMap<HSLColor, HSLInfo> getHslInfos() {
        return hslInfos;
    }

    public void setSplineCanvas(SplineCanvas splineCanvas) {
        this.splineCanvas = splineCanvas;
    }

    public static void saveImagePath(String imagePath, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(imagePath);
            writer.newLine();  // 添加换行符，以便区分不同图片路径
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readImagePath(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
