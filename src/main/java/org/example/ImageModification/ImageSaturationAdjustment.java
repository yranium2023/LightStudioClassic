package org.example.ImageModification;

/**
 * @author 申雄全
 * @Description
 * @date 2023/12/9 15:00
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ImageTools.ImageTransfer;

import java.awt.image.BufferedImage;
import java.util.concurrent.*;

public class ImageSaturationAdjustment extends Application {
    private ImageView imageView;
    private BufferedImage bufferedImage;
    private BufferedImage processedImage;
    private double lastValue=1;
    private double saturationValue;

    @Override
    public void start(Stage primaryStage) {
        Image originalImage = new Image(getClass().getResource("/image/a.jpg").toString());
        imageView = new ImageView(originalImage);
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);
        bufferedImage = ImageTransfer.toBufferedImage(imageView.getImage());
        processedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Slider saturationSlider = new Slider(0.0, 2, 1);
        double threshold =0.1;
        saturationSlider.setOnMouseDragged(event -> {
            double newValue=saturationSlider.getValue();
            if(Math.abs(newValue-lastValue)>threshold){
                saturationValue=saturationSlider.getValue();
                adjustSaturationAsync();
                lastValue=newValue;
            }
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(imageView, saturationSlider);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Image Saturation Adjustment");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void adjustSaturationAsync() {

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> {
            ForkJoinPool forkJoinPool=new ForkJoinPool();
            forkJoinPool.invoke(new SaturationTask(0,0,bufferedImage.getWidth(),bufferedImage.getHeight()));
            javafx.application.Platform.runLater(() -> {
                Image adjustedImage =ImageTransfer.toJavaFXImage(processedImage);
                imageView.setImage(adjustedImage);
            });
            bufferedImage.flush();
            processedImage.flush();
            forkJoinPool.shutdown();
        });
        executor.shutdown();
    }

    class SaturationTask extends RecursiveAction{
        private static final int Max =200000;
        private final int startX, startY, endX, endY;
        SaturationTask(int startX, int startY, int endX, int endY){
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
        @Override
        protected void compute() {
            if((endX-startX)*(endY-startY)<Max){
                for (int x = startX; x<endX;x++) {
                    for (int y=startY;y<endY;y++) {
                        int rgb = bufferedImage.getRGB(x, y);
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;
                        double[] hsl = rgbToHsl(red, green, blue);
                        hsl[1] *= saturationValue;
                        hsl[1] = Math.max(0, Math.min(1, hsl[1]));
                        rgb = hslToRgb(hsl[0], hsl[1], hsl[2], alpha);
                        processedImage.setRGB(x, y, rgb);
                    }
                }
            }else{
                int midX=(startX+endX)/2;
                int midY=(startY+endY)/2;
                ForkJoinTask<Void> A=new SaturationTask(startX, startY, midX, midY).fork();
                ForkJoinTask<Void> B=new SaturationTask(midX, startY, endX, midY).fork();
                ForkJoinTask<Void> C=new SaturationTask(startX, midY, midX, endY).fork();
                ForkJoinTask<Void> D=new SaturationTask(midX, midY, endX, endY).fork();
                A.join();B.join();C.join();D.join();
            }
        }
    }

    // 将 RGB 转换为 HSL
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

    public static void main(String[] args) {
        launch(args);
    }

}






