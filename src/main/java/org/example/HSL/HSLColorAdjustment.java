package org.example.HSL;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.ImageModification.ThreadProcess;
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

    private final double [] lastValue={0,0,0};

    private int selectedColor=0;

    private int selectedProperty;

    private double  huePer=0, satuPer=0,lumPer=0;
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
    //修改后请使之继承imageadjustment类，自行调整上述属性的假设。
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Image originalImage = new Image(getClass().getResource("/image/icon.png").toString());
        bufferedImage=ImageTransfer.toBufferedImage(originalImage);
        processedImage=new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);;
        HBox root = new HBox();
        root.setSpacing(10);
        //7个按钮
        Button buttonRed=new Button("红");
        Button buttonOrange=new Button("橙");
        Button buttonYellow=new Button("黄");
        Button buttonGreen=new Button("绿");
        Button buttonBlue=new Button("蓝");
        Button buttonCyan=new Button("蓝绿");
        Button buttonPurple=new Button("紫");
        //七个按钮和相应的颜色对应
        setButton(buttonRed,MyColor.Red);
        setButton(buttonOrange,MyColor.Orange);
        setButton(buttonYellow,MyColor.Yellow);
        setButton(buttonGreen,MyColor.Green);
        setButton(buttonBlue,MyColor.Blue);
        setButton(buttonCyan,MyColor.Cyan);
        setButton(buttonPurple,MyColor.Purple);


        //请合理设置参数，最大范围均为-1到1
        Slider sliderHue=new Slider(-0.18,0.18,0);
        Slider sliderSaturation=new Slider(-0.3,0.3,0);
        Slider sliderLuminance=new Slider(-0.2,0.2,0);
        //判断间隔
        double threshold =0.02;
        //色相轴
        sliderHue.setOnMouseDragged(dragEvent -> {
            huePer=sliderHue.getValue();
            selectedProperty=0;
            if(Math.abs(huePer-lastValue[0])>threshold){
                HSLAdjust();
                //System.out.println("done");
                lastValue[0]=huePer;
            }
        });
        //饱和度轴
        sliderSaturation.setOnMouseDragged(dragEvent -> {
            satuPer=sliderSaturation.getValue();
            selectedProperty=1;
            if(Math.abs(satuPer-lastValue[1])>threshold){
                HSLAdjust();
                lastValue[1]=satuPer;
            }
        });
        //亮度轴
        sliderLuminance.setOnMouseDragged((dragEvent -> {
            lumPer=sliderLuminance.getValue();
            selectedProperty=2;
            if(Math.abs(lumPer-lastValue[2])>threshold){
                HSLAdjust();
                lastValue[2]=lumPer;
            }
        }));
        imageView=new ImageView(originalImage);
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        root.getChildren().addAll(imageView,buttonRed,buttonBlue,buttonGreen,buttonYellow,buttonOrange,buttonPurple,buttonCyan,sliderHue,sliderSaturation,sliderLuminance);
        //root.getChildren().addAll(imageView,buttonRed,buttonBlue,buttonGreen,buttonOrange,buttonPurple,buttonYellow,buttonCyan,sliderHue,sliderLuminance,sliderLuminance);
        Scene scene = new Scene(root);
        primaryStage.setTitle("HSL Color Adjustments");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void HSLAdjust(){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(()->{
            new ThreadProcess(bufferedImage,processedImage){
                @Override
                public int calculateRGB(int rgb) {
                    if(isColor[selectedColor].doSomething(rgb)){
                        int alpha = (rgb >> 24) & 0xFF;
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                       double []hsl=rgbToHsl(r,g,b);
                       switch (selectedProperty){
                           case 0:
                               hsl[0] += huePer;
                               if (hsl[0] > 1.0) {
                                   hsl[0] -= 1.0; // 如果超出了1，将其回归到0-1的范围内
                               } else if (hsl[0] < 0.0) {
                                   hsl[0] += 1;
                               }
                               break;
                           case 1:
                               hsl[1] +=satuPer;
                               hsl[1]=Math.max(0.0,Math.min(1.0,hsl[1]));

                               break;
                           default:
                               hsl[2] +=lumPer;
                               hsl[2]=Math.max(0.0,Math.min(1.0,hsl[2]));
                               break;
                       }

                        return hslToRgb(hsl[0],hsl[1],hsl[2],alpha);
                    }else{
                        return rgb;
                    }
                }
            }.run();
            javafx.application.Platform.runLater(() -> {
                Image adjustedImage = ImageTransfer.toJavaFXImage(processedImage);
                imageView.setImage(adjustedImage);
                System.out.println("DONE");
            });
        });
        executor.shutdown();
    }

    private void setButton(Button button,MyColor color){
        button.setOnAction(actionEvent -> {
            selectedColor=color.ordinal();
        });
    }
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

        if (del_Max == 0.0) {
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

        if (s ==0.0) {
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

            double var_1 = 2.0 * l - var_2;

            r = (int) (255.0 * hueToRgb(var_1, var_2, h + (1.0 / 3.0)));
            g = (int) (255.0 * hueToRgb(var_1, var_2, h));
            b = (int) (255.0 * hueToRgb(var_1, var_2, h - (1.0 / 3.0)));
        }

        return  (alpha << 24) | (r << 16) | (g << 8) | b;
    }

    private static double hueToRgb(double v1, double v2, double vH) {
        if (vH < 0.0) vH += 1.0;
        if (vH > 1.0) vH -= 1.0;
        if ((6.0 * vH) < 1.0) return (v1 + (v2 - v1) * 6.0 * vH);
        if ((2.0 * vH) < 1.0) return (v2);
        if ((3.0 * vH) < 2.0) return (v1 + (v2 - v1) * ((2.0 / 3.0) - vH) * 6.0);
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
