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
import org.example.Scene.ImageEditScene;
import org.example.Scene.ImageImportMenuScene;
import org.example.Scene.ImageImportScene;
import org.example.StaticValues;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 统一管理每个导入的图片 有其原图 精致压缩（用于编辑）和粗糙压缩（用于当图标使用）的图片
 *
 * @author 张喆宇
 * Date 2023/12/9 11:05
 */
public class ImageObj implements Serializable {
    private static final long serialVersionUID = 1L;
    String imagePath = null;
    transient String imageName = null;
    private transient Image originalImage = null;
    private transient Image buttonImage = null;
    private transient Image editingImage = null;
    private transient List<Image> clipImages = new ArrayList<>();
    private transient VBox buttonVBox = null;

    private transient FusionImageButton imageButton = null;

    private transient FusionImageButton copyButton = null;

    private transient VBox copyVBox = null;

    private transient VBox outPutImageVBox = null;

    private transient double contrastPercent = 0.5;

    private transient double exposurePercent = 0.5;

    private transient double saturationPercent = 0.5;

    private transient double temperaturePercent = 0.5;

    private transient sliderType_1 nowSlider_1 = null;
    //历史记录
    private List<AdjustHistory> adjustHistory = new ArrayList<>();

    private Map<String, AdjustHistory> adjustHistoryMap = new HashMap<>();
    private transient SplineCanvas splineCanvas = new SplineCanvas(190);
    private final transient HashMap<HSLColor, HSLInfo> hslInfos = new HashMap<>() {{
        put(HSLColor.Red, new HSLInfo(HSLColor.Red));
        put(HSLColor.Yellow, new HSLInfo(HSLColor.Yellow));
        put(HSLColor.Orange, new HSLInfo(HSLColor.Orange));
        put(HSLColor.Green, new HSLInfo(HSLColor.Green));
        put(HSLColor.Cyan, new HSLInfo(HSLColor.Cyan));
        put(HSLColor.Blue, new HSLInfo(HSLColor.Blue));
        put(HSLColor.Purple, new HSLInfo(HSLColor.Purple));
    }};


    /***
     *  构造函数 用于构建Image对象
     * @param originalImage
     * @author 张喆宇
     * Date 2023/12/9 11:09
     **/

    public ImageObj(Image originalImage) {
        this.originalImage = originalImage;
        this.imagePath = originalImage.getUrl();
    }

    /***
     *  用于压缩图片 普通压缩
     * @param image
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * Date 2023/12/9 21:47
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
     *  用于压缩图片 按钮级别压缩
     * @param image
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * Date 2023/12/9 21:49
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

    /***
     *  获取原图
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * Date 2023/12/9 11:14
     **/
    public Image getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(Image originalImage) {
        this.originalImage = originalImage;
    }

    /***
     *  获取按钮图片
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * Date 2023/12/9 11:14
     **/

    public Image getButtonImage() {
        return buttonImage;
    }

    /**
     * 传入按钮图片
     *
     * @param buttonImage
     * @author 张喆宇
     * Date 2023/12/9 11:13
     **/
    public void setButtonImage(Image buttonImage) {
        this.buttonImage = buttonImage;
    }

    /***
     *  获取编辑中图片
     * @return javafx.scene.image.Image
     * @author 张喆宇
     * Date 2023/12/9 11:14
     **/

    public Image getEditingImage() {
        return editingImage;
    }

    /**
     * 传入编辑用图片
     *
     * @param editingImage
     * @author 张喆宇
     * Date 2023/12/9 11:13
     **/
    public void setEditingImage(Image editingImage) {
        this.editingImage = editingImage;
    }

    /***
     *  获取图片路径
     * @return java.lang.String
     * @author 张喆宇
     * Date 2023/12/9 11:14
     **/

    public String getImagePath() {
        return imagePath;
    }

    /***
     *  获取裁剪图片列表
     * @author 张喆宇
     * Date 2023/12/9 11:26
     **/

    public List<Image> getClipImages() {
        return clipImages;
    }

    public void setClipImages(List<Image> clipImages) {
        this.clipImages = clipImages;
    }

    /***
     *  更新图片
     * @return null
     * @author 张喆宇
     * Date 2023/12/10 0:32
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
     * @param nowImage 这个类用来生成新的压缩图片、图标图片、直方图、和图片面熟
     * @author 吴鹄远
     * Date 2023/12/11 15:24
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
        if (!this.getClipImages().isEmpty()) {
            Image newClipImage = this.getClipImages().get(this.clipImages.size() - 1);
            this.buttonVBox.getChildren().remove(1);
            Label descriptionLabel = new Label(Integer.toString((int) newClipImage.getWidth()) + '×' + (int) newClipImage.getHeight());
            descriptionLabel.setTextFill(Color.WHITE);
            this.buttonVBox.getChildren().add(descriptionLabel);
        }
    }

    /***
     *  用于从两个图片按钮以及总图片中移除所有产生的按钮
     * @author 张喆宇
     * Date 2023/12/12 22:17
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

    /***
     *  存入图库中按钮
     * @param imageButton
     * @author 张喆宇
     * Date 2023/12/10 0:30
     **/

    public void setImageButton(FusionImageButton imageButton) {
        this.imageButton = imageButton;
    }

    public FusionImageButton getCopyButton() {
        return copyButton;
    }

    /***
     *  存入横版按钮
     * @param copyButton
     * @author 张喆宇
     * Date 2023/12/10 0:30
     **/

    public void setCopyButton(FusionImageButton copyButton) {
        this.copyButton = copyButton;
    }

    public VBox getButtonVBox() {
        return buttonVBox;
    }

    public void setButtonVBox(VBox buttonVBox) {
        this.buttonVBox = buttonVBox;
    }

    public VBox getCopyVBox() {
        return copyVBox;
    }

    public void setCopyVBox(VBox copyVBox) {
        this.copyVBox = copyVBox;
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

    public void setSplineCanvas(SplineCanvas splineCanvas) {
        this.splineCanvas = splineCanvas;
    }

    public HashMap<HSLColor, HSLInfo> getHslInfos() {
        return hslInfos;
    }

    public List<AdjustHistory> getAdjustHistory() {
        return adjustHistory;
    }

    public void setAdjustHistory(List<AdjustHistory> adjustHistory) {
        this.adjustHistory = adjustHistory;
    }

    public Map<String, AdjustHistory> getAdjustHistoryMap() {
        return adjustHistoryMap;
    }

    public void setAdjustHistoryMap(Map<String, AdjustHistory> adjustHistoryMap) {
        this.adjustHistoryMap = adjustHistoryMap;
    }

    /**
     * 此方法用于添加历史记录
     *
     * @param History
     * @author 吴鹄远
     * Date 2023/12/18 19:58
     **/

    public void addHistory(AdjustHistory History) {
        adjustHistory.add(History);
        adjustHistoryMap.put(History.getAdjustProperty(), History);
    }

    /**
     * 此方法用于导出最高品质的图像
     *
     * @return javafx.scene.image.Image
     * @author 吴鹄远
     * Date 2023/12/18 14:33
     **/

    public Image AdjustRealImage() {
        Image tempImage = null;
        if (clipImages.isEmpty()) {
            tempImage = originalImage;
        } else {
            int lastIndex = clipImages.size() - 1;
            tempImage = clipImages.get(lastIndex);
        }

        ImageAdjustment.bufferedImage = ImageTransfer.toBufferedImage(tempImage);
        Set<Map.Entry<String, AdjustHistory>> entrySet = adjustHistoryMap.entrySet();
        Iterator<Map.Entry<String, AdjustHistory>> iterator = entrySet.iterator();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        while (iterator.hasNext()) {
            Map.Entry<String, AdjustHistory> entry = iterator.next();
            String key = entry.getKey();
            AdjustHistory value = entry.getValue();
            ImageAdjustment.bufferedImage = ImageTransfer.toBufferedImage(tempImage);
            ImageAdjustment.setProcessedImage();
            Future<?> future = executor.submit(() -> {
                imageToHistory(value);
            });
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            tempImage = ImageTransfer.toJavaFXImage(ImageAdjustment.processedImage);
        }
        executor.shutdown();
        return ImageTransfer.toJavaFXImage(ImageAdjustment.processedImage);
    }

    /**
     * 此方法用于裁剪过程中对裁剪好的图像进行渲染
     *
     * @author 吴鹄远
     * Date 2023/12/18 14:39
     **/

    public void editingImageToHistory() {
        AtomicReference<Image> tempImage = new AtomicReference<>(this.editingImage);
        ImageAdjustment.bufferedImage = ImageTransfer.toBufferedImage(tempImage.get());
        Set<Map.Entry<String, AdjustHistory>> entrySet = adjustHistoryMap.entrySet();
        Iterator<Map.Entry<String, AdjustHistory>> iterator = entrySet.iterator();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        while (iterator.hasNext()) {
            Map.Entry<String, AdjustHistory> entry = iterator.next();
            String key = entry.getKey();
            AdjustHistory value = entry.getValue();
            ImageAdjustment.bufferedImage = ImageTransfer.toBufferedImage(tempImage.get());
            ImageAdjustment.setProcessedImage();
            Future<?> future = executor.submit(() -> {
                imageToHistory(value);
            });
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            tempImage.set(ImageTransfer.toJavaFXImage(ImageAdjustment.processedImage));
        }
        executor.shutdown();
        renewAll(tempImage.get());
    }

    /**
     * 用于根据某一条历史记录调整图像。注意每次调整都需要刷新bufferedImage。
     *
     * @param history
     * @author 吴鹄远
     * Date 2023/12/18 10:05
     **/
    public void imageToHistory(AdjustHistory history) {
        var key = history.getAdjustProperty();
        var value = history;
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
        } else if ("HSL色相调整".equals(key.substring(0, key.length() - 1))) {
            //System.out.println(history.getAdjustProperty());
            HSLColorAdjustment.setSelectedColor(Character.getNumericValue(key.charAt(7)));
            //System.out.println(Character.getNumericValue(key.charAt(7)));
            HSLColorAdjustment.setHuePer(value.getFirstValue());
            HSLColorAdjustment.setSelectedProperty(0);
            HSLColorAdjustment.HSLAdjust();
        } else if ("HSL饱和度调整".equals(key.substring(0, key.length() - 1))) {
            HSLColorAdjustment.setSelectedColor(Character.getNumericValue(key.charAt(8)));
            HSLColorAdjustment.setSatuPer(value.getFirstValue());
            HSLColorAdjustment.setSelectedProperty(1);
            HSLColorAdjustment.HSLAdjust();
        } else if ("HSL明度调整".equals(key.substring(0, key.length() - 1))) {
            HSLColorAdjustment.setSelectedColor(Character.getNumericValue(key.charAt(7)));
            HSLColorAdjustment.setLumPer(value.getFirstValue());
            HSLColorAdjustment.setSelectedProperty(2);
            HSLColorAdjustment.HSLAdjust();
        } else if ("自动白平衡".equals(key)) {
            AutoWhiteBalance.WhiteBalance();
        }
    }

    /**
     * 该枚举类实现轴对应的类型
     *
     * @author 申雄全
     * Date 2023/12/24 1:01
     */
    public enum sliderType_1 {
        /**
         * 对比度轴
         */
        CONTRAST,
        /**
         * 曝光度轴
         */
        EXPOSURE,
        /**
         * 饱和度轴
         */
        SATURATION,
        /**
         * 色温轴
         */
        TEMPERATURE
    }

}
