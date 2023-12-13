package org.example.ImageModification;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.ImageTools.ImageTransfer;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 申雄全
 * @Description 该类实现HSL调整，对每个颜色调整色相，饱和度，亮度
 * @date 2023/12/9 13:48
 */
enum MyColor{
    Red,
    Orange,
    Yellow,
    Green,
    Cyan,
    Blue,
    Purple;

}
interface MyFunction {
    boolean doSomething(int rgb);

}
public class HSLColorAdjustment extends Application{
    private ImageView imageView;

    private BufferedImage bufferedImage;

    private BufferedImage processedImage;


    private int selectedValue;//0改变色相，1改变饱和度，2改变亮度

    private double adjustValue;

    private double [] lastValue={0,0,0};

    private int selectedColor;
    private static final MyFunction[] isColor=new MyFunction[7];
    static{
        isColor[0]=HSLColorAdjustment::isRed;
        isColor[1]=HSLColorAdjustment::isOrange;
        isColor[2]=HSLColorAdjustment::isYellow;
        isColor[3]=HSLColorAdjustment::isGreen;
        isColor[4]=HSLColorAdjustment::isBlue;
        isColor[5]=HSLColorAdjustment::isCyan;
        isColor[6]=HSLColorAdjustment::isPurple;

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Image originalImage = new Image(getClass().getResource("/image/icon.png").toString());
        bufferedImage=ImageTransfer.toBufferedImage(originalImage);
        processedImage=bufferedImage;
        HBox root = new HBox();
        root.setSpacing(10);

        Button buttonRed=new Button("红");
        Button buttonOrange=new Button("橙");
        Button buttonYellow=new Button("黄");
        Button buttonGreen=new Button("绿");
        Button buttonBlue=new Button("蓝");
        Button buttonCyan=new Button("蓝绿");
        Button buttonPurple=new Button("紫");
        setButton(buttonRed,MyColor.Red);
        setButton(buttonOrange,MyColor.Orange);
        setButton(buttonYellow,MyColor.Yellow);
        setButton(buttonGreen,MyColor.Green);
        setButton(buttonBlue,MyColor.Blue);
        setButton(buttonCyan,MyColor.Cyan);
        setButton(buttonPurple,MyColor.Purple);

        Slider sliderHue=new Slider(0.2,1.8,1);
        Slider sliderSaturation=new Slider(0.2,1.8,1);
        Slider sliderLuminance=new Slider(0.2,1.8,1);
        selectedColor=0;//初始颜色设为红色
        double threshold =0.1;
        selectedValue=-1;

        sliderHue.setOnMouseDragged(dragEvent -> {
                selectedValue=0;
            adjustValue=sliderHue.getValue();
            if(Math.abs(adjustValue-lastValue[0])>threshold){
                adjust();
                lastValue[0]=adjustValue;
            }
        });
        sliderSaturation.setOnMouseDragged(dragEvent -> {
            selectedValue=1;
            adjustValue=sliderSaturation.getValue();
            if(Math.abs(adjustValue-lastValue[1])>threshold){
                adjust();
                lastValue[1]=adjustValue;
            }
        });
        sliderLuminance.setOnMouseDragged((dragEvent -> {
            selectedValue=2;
            adjustValue=sliderLuminance.getValue();
            if(Math.abs(adjustValue-lastValue[2])>threshold){
                adjust();
                lastValue[2]=adjustValue;
            }
        }));
        imageView=new ImageView(originalImage);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        root.getChildren().addAll(imageView,buttonRed,buttonBlue,buttonGreen,buttonOrange,buttonPurple,buttonYellow,buttonCyan,sliderHue,sliderSaturation,sliderLuminance);

        Scene scene = new Scene(root);
        primaryStage.setTitle("HSL Color Adjustments");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void adjust(){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(()->{

            new ThreadProcess(bufferedImage,processedImage){
                @Override
                public int calculateRGB(int rgb) {
                    if(isColor[selectedColor].doSomething(rgb)){
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;
                        double[] hsl = rgbToHsl(red, green, blue);
                        hsl[selectedValue]*=adjustValue;
                        hsl[selectedValue] = Math.max(0.1, Math.min(1, hsl[selectedValue]));
                        return hslToRgb(hsl[0], hsl[1], hsl[2], alpha);
                    }else{
                        return rgb;
                    }
                }
            }.run();
            javafx.application.Platform.runLater(() -> {
                Image adjustedImage = ImageTransfer.toJavaFXImage(processedImage);
                imageView.setImage(adjustedImage);
            });
        });
        executor.shutdown();
    }

    private void setButton(Button button,MyColor color){
        button.setOnAction(actionEvent -> {
            selectedColor=color.ordinal();
        });
    }

    /*private static double[] rgbToHsl(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        double _r = r / 255.0;
        double _g = g / 255.0;
        double _b = b / 255.0;

        double max = Math.max(_r, Math.max(_g, _b));
        double min = Math.min(_r, Math.min(_g, _b));
        double delta = max - min;

        double h, s, l;

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

        return new double[]{h * 360, s, l};
    }

    private static int hslToRgb(double[] hsl,int alpha) {
        double h = hsl[0] / 360;
        double s = hsl[1];
        double l = hsl[2];

        double r, g, b;

        if (s == 0) {
            r = g = b = l;
        } else {
            double q = (l < 0.5) ? (l * (1 + s)) : (l + s - l * s);
            double p = 2 * l - q;
            r = hueToRgb(p, q, h + 1.0f / 3.0f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1.0f / 3.0f);
        }
        int red = Math.round((float) r * 255);
        int green = Math.round((float) g * 255);
        int blue = Math.round((float) b * 255);
        return new Color(red, green, blue).getRGB();
    }

    private static double hueToRgb(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1.0 / 6.0) return p + (q - p) * 6 * t;
        if (t < 1.0 / 2.0) return q;
        if (t < 2.0 / 3.0) return p + (q - p) * (2.0f / 3.0f - t) * 6;
        return p;
    }*/
    private static double[] rgbToHsl(int r, int g, int b) {
        double[] hsl = new double[3];
        double var_R = (r / 255.0);
        double var_G = (g / 255.0);
        double var_B = (b / 255.0);

        double var_Min = Math.min(var_R, Math.min(var_G, var_B));
        double var_Max = Math.max(var_R, Math.max(var_G, var_B));
        double del_Max = var_Max - var_Min;

        double H, S, L;
        L = (var_Max + var_Min) / 2.0;

        if (del_Max - 0.01 <= 0.0) {
            H = 0;
            S = 0;
        } else {
            if (L < 0.5) {
                S = del_Max / (var_Max + var_Min);
            } else {
                S = del_Max / (2 - var_Max - var_Min);
            }
            double del_R = (((var_Max - var_R) / 6.0) + (del_Max / 2.0)) / del_Max;
            double del_G = (((var_Max - var_G) / 6.0) + (del_Max / 2.0)) / del_Max;
            double del_B = (((var_Max - var_B) / 6.0) + (del_Max / 2.0)) / del_Max;
            if (var_R == var_Max) {
                H = del_B - del_G;
            } else if (var_G == var_Max) {
                H = (1.0 / 3.0) + del_R - del_B;
            } else {
                H = (2.0 / 3.0) + del_G - del_R;
            }
            if (H < 0) {
                H += 1;
            }
            if (H > 1) {
                H -= 1;
            }
        }
        hsl[0] = H;
        hsl[1] = S;
        hsl[2] = L;
        return hsl;
    }

    // 将 HSL 转换回 RGB
    private static int hslToRgb(double h, double s, double l,int alpha) {
        int r, g, b;

        if (s <= 0.01) {
            r = (int) (l * 255.0);
            g = (int) (l * 255.0);
            b = (int) (l * 255.0);
        } else {
            double var_2;
            if (l < 0.5) {
                var_2 = l * (1 + s);
            } else {
                var_2 = (l + s) - (s * l);
            }

            double var_1 = 2 * l - var_2;

            r = (int) (255.0 * hueToRgb(var_1, var_2, h + (1.0 / 3.0)));
            g = (int) (255.0 * hueToRgb(var_1, var_2, h));
            b = (int) (255.0 * hueToRgb(var_1, var_2, h - (1.0 / 3.0)));
        }

        return  (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    private static double hueToRgb(double v1, double v2, double vH) {
        if (vH < 0.0) {
            vH += 1.0;
        }
        if (vH > 1.0) {
            vH -= 1.0;
        }
        if ((6.0 * vH) < 1.0) {
            return (v1 + (v2 - v1) * 6.0 * vH);
        }
        if ((2.0 * vH) < 1.0) {
            return (v2);
        }
        if ((3.0 * vH) < 2.0) {
            return (v1 + (v2 - v1) * ((2.0 / 3.0) - vH) * 6.0);
        }
        return (v1);
    }

    // 判断红色
    private static boolean isRed(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red > 150 && green < 100 && blue < 100;
    }
    // 判断橙色
    private static boolean isOrange(int rgb){
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red > 200 && green > 80 && blue < 50;
    }
    // 判断黄色
    private static boolean isYellow(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red > 200 && green > 180 && blue < 50;
    }
    // 判断绿色
    private static boolean isGreen(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red < 100 && green > 150 && blue < 100;
    }
    // 判断蓝色
    private static boolean isBlue(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red < 100 && green < 100 && blue > 150;
    }
    // 判断靛色
    private static boolean isCyan(int rgb){
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return green > 150 && blue > 150 && red < 100;
    }
    // 判断紫色
    private static boolean isPurple(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red > 150 && green < 100 && blue > 150;
    }
}
