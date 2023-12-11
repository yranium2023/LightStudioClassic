package org.example.ImageModification;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 申雄全
 * @Description 该类实现HSL调整
 * @date 2023/12/9 13:48
 */

public class HSLColorAdjustment extends Application {

    private static final Map<String, float[]> colorAdjustments = new HashMap<>();

    static {
        // 在这里设置不同颜色的 HSL 调整参数
        // {hueAdjustment, saturationAdjustment, luminanceAdjustment}
        colorAdjustments.put("Red", new float[]{0.0f, 0.5f, 0.0f});
        colorAdjustments.put("Orange", new float[]{30.0f, 0.5f, 0.0f});
        colorAdjustments.put("Yellow", new float[]{60.0f, 0.5f, 0.0f});
        colorAdjustments.put("Green", new float[]{120.0f, 0.5f, 0.0f});
        colorAdjustments.put("Cyan", new float[]{180.0f, 0.5f, 0.0f});
        colorAdjustments.put("Blue", new float[]{240.0f, 0.5f, 0.0f});
        colorAdjustments.put("Purple", new float[]{270.0f, 0.5f, 0.0f});
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 替换为实际图片的路径
        Image originalImage = new Image("path_to_your_image.jpg");

        HBox root = new HBox();
        root.setSpacing(10);

        for (Map.Entry<String, float[]> entry : colorAdjustments.entrySet()) {
            String colorName = entry.getKey();
            float[] hslAdjustments = entry.getValue();

            Image adjustedImage = adjustImage(originalImage, hslAdjustments[0], hslAdjustments[1], hslAdjustments[2]);
            ImageView imageView = new ImageView(adjustedImage);
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            root.getChildren().add(imageView);
        }

        Scene scene = new Scene(root);
        primaryStage.setTitle("HSL Color Adjustments");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Image adjustImage(Image inputImage, float hueAdjustment, float saturationAdjustment, float luminanceAdjustment) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(inputImage, null);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = bufferedImage.getRGB(x, y);
                Color color = new Color(rgb);
                // 将 RGB 转换为 HSL
                float[] hsl = rgbToHsl(color);

                // 对 HSL 进行调整
                hsl[0] += hueAdjustment;
                hsl[1] += saturationAdjustment;
                hsl[2] += luminanceAdjustment;

                // 将调整后的 HSL 转换回 RGB
                int newRGB = hslToRgb(hsl);
                bufferedImage.setRGB(x, y, newRGB);
            }
        }
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private float[] rgbToHsl(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        float _r = r / 255.0f;
        float _g = g / 255.0f;
        float _b = b / 255.0f;

        float max = Math.max(_r, Math.max(_g, _b));
        float min = Math.min(_r, Math.min(_g, _b));
        float delta = max - min;

        float h, s, l;

        // 计算亮度（Luminance）
        l = (max + min) / 2;

        // 如果最大和最小值相同，表示色彩为灰色，色相和饱和度为0
        if (delta == 0) {
            h = s = 0;
        } else {
            // 计算饱和度（Saturation）
            s = delta / (1 - Math.abs(2 * l - 1));

            // 计算色相（Hue）
            if (max == _r) {
                h = ((_g - _b) / delta + ((_g < _b) ? 6 : 0)) / 6;
            } else if (max == _g) {
                h = ((_b - _r) / delta + 2) / 6;
            } else {
                h = ((_r - _g) / delta + 4) / 6;
            }
        }

        return new float[]{h * 360, s, l};
    }

    private int hslToRgb(float[] hsl) {
        float h = hsl[0] / 360;
        float s = hsl[1];
        float l = hsl[2];

        float r, g, b;

        if (s == 0) {
            r = g = b = l;
        } else {
            float q = (l < 0.5) ? (l * (1 + s)) : (l + s - l * s);
            float p = 2 * l - q;

            r = hueToRgb(p, q, h + 1.0f / 3.0f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0f / 3.0f);
        }

        int red = Math.round(r * 255);
        int green = Math.round(g * 255);
        int blue = Math.round(b * 255);

        return new Color(red, green, blue).getRGB();
    }

    private float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0 / 6.0) return p + (q - p) * 6 * t;
        if (t < 1.0 / 2.0) return q;
        if (t < 2.0 / 3.0) return p + (q - p) * (2.0f / 3.0f - t) * 6;
        return p;
    }
}
