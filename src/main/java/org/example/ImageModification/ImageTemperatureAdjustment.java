package org.example.ImageModification;


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


/**
 * @author 申雄全
 * @Description 该类实现色相调整
 * @date 2023/12/8 22:38
 */
public class ImageTemperatureAdjustment extends Application {
    private ImageView imageView;
    private BufferedImage bufferedImage;
    private BufferedImage processedImage;//处理完的图形
    private  double lastValue;
    private double Temperature;//色温
    private  double originalTemperature;
    private double redStrength, greenStrength, blueStrength;

    @Override
    public void start(Stage primaryStage) {
        Image originalImage = new Image(getClass().getResource("/image/b.jpg").toString());
        imageView = new ImageView(originalImage);
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);
        bufferedImage = ImageTransfer.toBufferedImage(imageView.getImage());
        processedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Slider temperatureSlider = getTemperatureSlider();
        VBox root = new VBox(10);
        root.getChildren().addAll(imageView, temperatureSlider);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Image Exposure Adjustment");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private double calculateColorTemperature() {
        int totalPixels = bufferedImage.getWidth() * bufferedImage.getHeight();
        int width= bufferedImage.getWidth(),height= bufferedImage.getHeight();
        int redTotal = 0;
        int greenTotal = 0;
        int blueTotal = 0;
        for (int x = 0; x < width; x++) {
            for (int y= 0; y < height; y++) {
                int rgb = bufferedImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                redTotal += red;
                greenTotal += green;
                blueTotal += blue;
            }
        }
        double r = (double) redTotal /totalPixels / 255.0;
        double g = (double) greenTotal / totalPixels / 255.0;
        double b = (double) blueTotal / totalPixels / 255.0;
        double x = (-0.14282) * r + (1.54924) * g + (-0.95641) * b;
        double y = (-0.32466) * r + (1.57837) * g + (-0.73191) * b;
        // 根据Y值计算相关色温
        double n = (x - 0.3320) / (y - 0.1858);
        return (449 * Math.pow(n, 3)) + (3525 * Math.pow(n, 2)) + (6823.3 * n) + 5520.33; // 返回色温值（单位：Kelvin）
    }
    private Slider getTemperatureSlider() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
           originalTemperature = calculateColorTemperature();
        });
        executor.shutdown();
        lastValue=5;
        Slider temperatureSlider = new Slider(0.15,1.85,1);
        double threshold =0.1; // 定义阈值，每次滑动长度大于该值时认为值发生改变
        temperatureSlider.setOnMouseDragged(event-> {
            // Update the exposure when the slider value changes
            double newValue=temperatureSlider.getValue();
            if(Math.abs(newValue-lastValue)>threshold){
                Temperature =originalTemperature*(2-newValue);
                adjustExposureAsync();
                lastValue=newValue;
            }
        });
        return temperatureSlider;
    }


    private void adjustExposureAsync() {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            double kelvin = Temperature / 100;
            if (kelvin < 66) {
                redStrength = 255;
            } else {
                redStrength = kelvin - 60;
                redStrength = 329.698727446 * Math.pow(redStrength, -0.1332047592);
                redStrength=Math.max(0,Math.min(redStrength,255));
            }
            if (kelvin < 66) {
                greenStrength = kelvin;
                greenStrength = 99.4708025861 * Math.log(greenStrength) - 161.1195681661;
                greenStrength=Math.max(0,Math.min(greenStrength,255));
            } else {
                greenStrength = kelvin - 60;
                greenStrength = 288.1221695283 * Math.pow(greenStrength, -0.0755148492);
                greenStrength=Math.max(0,Math.min(greenStrength,255));
            }
            if (kelvin >= 66) {
                blueStrength = 255;
            } else {
                if (kelvin <= 19) {
                    blueStrength = 0;
                } else {
                    blueStrength = kelvin - 10;
                    blueStrength = 138.5177312231 * Math.log(blueStrength) - 305.0447927307;
                    blueStrength=Math.max(0,Math.min(blueStrength,255));
                }
            }
            ForkJoinPool forkJoinPool=new ForkJoinPool();

            forkJoinPool.invoke(new ExposureTask(0,0,bufferedImage.getWidth(),bufferedImage.getHeight()));

            javafx.application.Platform.runLater(() -> {
                Image adjustedImage =ImageTransfer.toJavaFXImage(processedImage);
                imageView.setImage(adjustedImage);
                // 释放资源
            });
            bufferedImage.flush();
            processedImage.flush();
            forkJoinPool.shutdown();
        });
        executor.shutdown();
    }
    class ExposureTask extends RecursiveAction {
        private static final int Max =200000;
        private final int startX, startY, endX, endY;
        ExposureTask(int startX, int startY, int endX, int endY){
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
                        int rgb =bufferedImage.getRGB(x, y);
                        // Extract individual color components
                        int alpha = (rgb >> 24) & 0xFF;
                        int red = (rgb >> 16) & 0xFF;
                        int green = (rgb >> 8) & 0xFF;
                        int blue = rgb & 0xFF;
                        red = (int) (red * (redStrength / 255));
                        green = (int) (green * (greenStrength / 255));
                        blue = (int) (blue * (blueStrength / 255));
                        // Compose the adjusted color
                        int adjustedRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        // Set the adjusted color to the pixel
                        processedImage.setRGB(x, y, adjustedRgb);
                    }
                }
            }else{
                int midX=(startX+endX)/2;
                int midY=(startY+endY)/2;
                ForkJoinTask<Void> A=new ExposureTask(startX, startY, midX, midY).fork();
                ForkJoinTask<Void> B=new ExposureTask(midX, startY, endX, midY).fork();
                ForkJoinTask<Void> C=new ExposureTask(startX, midY, midX, endY).fork();
                ForkJoinTask<Void>  D=new ExposureTask(midX, midY, endX, endY).fork();
                A.join();B.join();C.join();D.join();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

