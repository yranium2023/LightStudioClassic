package org.example.Obj;

import io.vproxy.vfx.ui.button.FusionImageButton;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.Curve.SplineCanvas.SplineBrightnessAdjustment;
import org.example.Curve.SplineCanvas.SplineCanvas;
import org.example.HSL.HSLColorAdjustment;
import org.example.ImageModification.*;
import org.example.ImageStatistics.Histogram;
import org.example.ImageTools.ConvertUtil;
import org.example.ImageTools.ImageTransfer;
import org.example.Scene.EditHistoryScene;
import org.example.Scene.ImageEditScene;
import org.example.Scene.ImageImportMenuScene;
import org.example.Scene.ImageImportScene;
import org.example.StaticValues;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * @author 张喆宇
 * @Description: 统一管理每个导入的图片 有其原图 精致压缩（用于编辑）和粗糙压缩（用于当图标使用）的图片
 * @date 2023/12/9 11:05
 */
public class ImageObj implements Serializable {
    private static final long serialVersionUID = 1L;
    //传入的原始图片
    private transient Image originalImage = null;
    //压缩到80*80的小图片
    private transient Image buttonImage = null;
    //压缩到2k的大图片
    private transient Image editingImage = null;
    //裁减过程中产生的图片列表
    private transient List<Image> clipImages = new ArrayList<>();
    //传入图片的路径
    String imagePath = null;
    //图片的名称
    transient String imageName = null;
    //图库中整个vbox
    private transient VBox buttonVBox = null;
    //图库中按钮
    private transient FusionImageButton imageButton = null;
    //横板按钮
    private transient FusionImageButton copyButton = null;
    //横版中整个vbox
    private transient VBox copyVBox = null;
    //导出图片中整个vbox
    private transient VBox outPutImageVBox = null;
    //对比度滑动条，初始化为0.5
    private transient double contrastPercent = 0.5;
    //曝光度滑动条，初始化为0.5
    private transient double exposurePercent = 0.5;
    //饱和度滑动条，初始化为0.5
    private transient double saturationPercent = 0.5;
    //色温滑动条，初始化为0.5
    private transient double temperaturePercent = 0.5;
    //创建一个枚举类型，存储当前滑动条是四个滑动条中的哪一条
    private transient sliderType_1 nowSlider_1 = null;
    //历史记录
    private final Stack<AdjustHistory> adjustHistory = new Stack<>();

    private final Map<String,AdjustHistory> adjustHistoryMap=new HashMap<>();
    public enum sliderType_1 {
        CONTRAST,
        EXPOSURE,
        SATURATION,
        TEMPERATURE
    }

    //创建一个属于自己的曲线，这样就不用记录了
    private transient SplineCanvas splineCanvas = new SplineCanvas(190);


    private transient HashMap<HSLColor,HSLInfo> hslInfos = new HashMap<>() {{
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
        if(!this.getClipImages().isEmpty()){
            Image newClipImage=this.getClipImages().get(this.clipImages.size()-1);
            this.buttonVBox.getChildren().remove(1);
            Label descriptionLabel = new Label(Integer.toString((int) newClipImage.getWidth()) + '×' + (int) newClipImage.getHeight());
            descriptionLabel.setTextFill(Color.WHITE);
            this.buttonVBox.getChildren().add(descriptionLabel);
        }
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

    public Stack<AdjustHistory> getAdjustHistory() {
        return adjustHistory;
    }

    public void setSplineCanvas(SplineCanvas splineCanvas) {
        this.splineCanvas = splineCanvas;
    }

    public void setOriginalImage(Image originalImage) {
        this.originalImage = originalImage;
    }

    // 序列化方法，接受一个包含多个 ImageObj 对象的列表，将它们保存到指定文件夹，并清空序列化文件
    public static void serializeImageObjs(List<ImageObj> imageObjs, String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            // 写入整个列表
            oos.writeObject(imageObjs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 反序列化方法，返回一个包含多个 ImageObj 对象的列表
    public static List<ImageObj> deserializeImageObjs(String filePath) {
        List<ImageObj> imageObjs = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            // 读取整个列表
            imageObjs = (List<ImageObj>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return imageObjs;
    }
    public void addHistory(AdjustHistory History){
        adjustHistory.push(History);
        adjustHistoryMap.put(History.getAdjustProperty(), History);
        EditHistoryScene.addLabel(History);
    }
    /**
     * @Description  此方法用于导出最高品质的图像
     * @return javafx.scene.image.Image
     * @author 吴鹄远
     * @date 2023/12/18 14:33
    **/

    public Image AdjustRealImage(){
        Image tempImage=null;
        if(clipImages.isEmpty()){
            tempImage=this.originalImage;
        }else{
            int lastIndex=clipImages.size()-1;
            tempImage=this.clipImages.get(lastIndex);
        }
        ImageAdjustment.bufferedImage=ImageTransfer.toBufferedImage(tempImage);
        Set<Map.Entry<String, AdjustHistory>> entrySet = adjustHistoryMap.entrySet();
        Iterator <Map.Entry<String,AdjustHistory>> iterator=entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, AdjustHistory> entry = iterator.next();
            String key = entry.getKey();
            AdjustHistory value=entry.getValue();
            ImageAdjustment.bufferedImage=ImageTransfer.toBufferedImage(tempImage);
            ImageAdjustment.setProcessedImage();
            imageToHistory(value);
            tempImage= ImageTransfer.toJavaFXImage(ImageAdjustment.processedImage);
        }
        return ImageTransfer.toJavaFXImage(ImageAdjustment.processedImage);
    }
    /**
     * @Description  此方法用于裁剪过程中对裁剪好的图像进行渲染
     * @author 吴鹄远
     * @date 2023/12/18 14:39
    **/

    public void editingImageToHistory(){
        Image tempImage=this.editingImage;
        ImageAdjustment.bufferedImage=ImageTransfer.toBufferedImage(tempImage);
        Set<Map.Entry<String, AdjustHistory>> entrySet = adjustHistoryMap.entrySet();
        Iterator <Map.Entry<String,AdjustHistory>> iterator=entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, AdjustHistory> entry = iterator.next();
            String key = entry.getKey();
            AdjustHistory value=entry.getValue();
            ImageAdjustment.bufferedImage=ImageTransfer.toBufferedImage(tempImage);
            ImageAdjustment.setProcessedImage();
            imageToHistory(value);
            tempImage= ImageTransfer.toJavaFXImage(ImageAdjustment.processedImage);
        }
        renewAll(tempImage);
    }

    /**
     * @Description  用于根据某一条历史记录调整图像。注意每次调整都需要刷新bufferedImage。
     * @param history
     * @author 吴鹄远
     * @date 2023/12/18 10:05
    **/
    public void imageToHistory(AdjustHistory history){
        var key=history.getAdjustProperty();
        var value=history;
        if ("点曲线调整".equals(key)) {
            SplineCanvas.setResultLUT(value.getLUTValue());
            SplineBrightnessAdjustment.applyLUTToImage();
        } else if ("对比度调整".equals(key)) {
            ImageContrastAdjustment.setContrastValue(value.getFirstValue());
            ImageContrastAdjustment.adjustContrastAsync();
        } else if ("饱和度调整".equals(key)) {
            ImageSaturationAdjustment.setSaturationValue(value.getFirstValue());
            ImageSaturationAdjustment.adjustSaturationAsync();
        } else if ("曝光度调整".equals(key)) {
            ImageExposureAdjustment.setExposureValue(value.getFirstValue());
            ImageExposureAdjustment.adjustExposureAsync();
        } else if ("色温调整".equals(key)) {
            ImageTemperatureAdjustment.setKelvin(value.getFirstValue());
            ImageTemperatureAdjustment.adjustTemperatureAsync();
        } else if ("HSL色相调整".equals(key.substring(0,key.length()-1))) {
            HSLColorAdjustment.setSelectedColor(Character.getNumericValue(key.charAt(7)));
            HSLColorAdjustment.setHuePer(value.getFirstValue());
            HSLColorAdjustment.setSelectedProperty(0);
            HSLColorAdjustment.HSLAdjust();
        } else if ("HSL饱和度调整".equals(key.substring(0,key.length()-1))) {
            HSLColorAdjustment.setSelectedColor(Character.getNumericValue(key.charAt(8)));
            HSLColorAdjustment.setSatuPer(value.getFirstValue());
            HSLColorAdjustment.setSelectedProperty(1);
            HSLColorAdjustment.HSLAdjust();
        } else if ("HSL明度调整".equals(key.substring(0,key.length()-1))) {
            HSLColorAdjustment.setSelectedColor(Character.getNumericValue(key.charAt(7)));
            HSLColorAdjustment.setLumPer(value.getFirstValue());
            HSLColorAdjustment.setSelectedProperty(2);
            HSLColorAdjustment.HSLAdjust();
        }
    }

}
