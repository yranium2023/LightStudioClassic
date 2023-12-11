package org.example.ImageModification;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ImageTools.ImageTransfer;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageExposureAdjustment extends ImageAdjustment {

    private Slider exposureSlider;
    private double lastValue = 0.0;
    private double exposureValue;

    public void start(Stage primaryStage) {
        Image originalImage = new Image(getClass().getResource("/image/icon.png").toString());
        imageView = new ImageView(originalImage);
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);
        bufferedImage = ImageTransfer.toBufferedImage(imageView.getImage());
        processedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        exposureSlider =new Slider(-1,1,0);
        double threshold = 0.1; // 定义阈值，每次滑动长度大于该值时认为值发生改变
        exposureSlider.setOnMouseDragged(event-> {
            // Update the exposure when the slider value changes
            double newValue=exposureSlider.getValue();
            if(Math.abs(newValue-lastValue)>threshold){
                exposureValue = newValue;
                adjustExposureAsync();
                // Update the ImageView with the adjusted image
                lastValue=newValue;
            }
        });
        VBox root = new VBox(10);
        root.getChildren().addAll(imageView, exposureSlider);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Image Exposure Adjustment");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void adjustExposureAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
           new ThreadProcess(bufferedImage,processedImage){
               @Override
               public int calculateRGB(int rgb) {
                   int alpha = (rgb >> 24) & 0xFF;
                   int red = (rgb >> 16) & 0xFF;
                   int green = (rgb >> 8) & 0xFF;
                   int blue = rgb & 0xFF;
                   // Adjust the brightness (exposure)
                   red = (int) (red * (1 + exposureValue));
                   green = (int) (green * (1 + exposureValue));
                   blue = (int) (blue * (1 + exposureValue));
                   // Clamp the values to the valid range [0, 255]
                   red = Math.max(0, Math.min(255, red));
                   green = Math.max(0, Math.min(255, green));
                   blue = Math.max(0, Math.min(255, blue));
                   // Compose the adjusted color
                   return (alpha << 24) | (red << 16) | (green << 8) | blue;
               }
           }.run();
            javafx.application.Platform.runLater(() -> {
                Image adjustedImage = SwingFXUtils.toFXImage(processedImage, null);
                imageView.setImage(adjustedImage);

            });
        });
        executor.shutdown();
    }


}
