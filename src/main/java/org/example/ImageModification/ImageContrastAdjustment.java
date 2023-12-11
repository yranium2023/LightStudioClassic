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

public class ImageContrastAdjustment extends ImageAdjustment {

    private Slider contrastSlider;

    private double lastValue = 0.0;
    private double contrastValue;

    public void start(Stage primaryStage) {
        Image originalImage = new Image(getClass().getResource("/image/a.jpg").toString());
        imageView = new ImageView(originalImage);
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);
        bufferedImage = ImageTransfer.toBufferedImage(imageView.getImage());
        processedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        contrastSlider = new Slider( 0.2, 1.8,1.0);
        double threshold = 0.1; // 定义阈值，每次滑动长度大于该值时认为值发生改变
        contrastSlider.setOnMouseDragged(event -> {
            // Update the contrast when the slider value changes
            double newValue = contrastSlider.getValue();
            if (Math.abs(newValue - lastValue) > threshold) {
                contrastValue = 2-newValue;
                adjustContrastAsync();
                // Update the ImageView with the adjusted image
                lastValue = newValue;
            }
        });
        VBox root = new VBox(10);
        root.getChildren().addAll(imageView, contrastSlider);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Image Contrast Adjustment");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void adjustContrastAsync() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            new ThreadProcess(bufferedImage, processedImage) {
                @Override
                public int calculateRGB(int rgb) {
                    int alpha = (rgb >> 24) & 0xFF;
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // Adjust the contrast
                    red = (int) (Math.pow(red / 255.0, 1.0 / contrastValue) * 255.0);
                    green = (int) (Math.pow(green / 255.0, 1.0 / contrastValue) * 255.0);
                    blue = (int) (Math.pow(blue / 255.0, 1.0 / contrastValue) * 255.0);
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
