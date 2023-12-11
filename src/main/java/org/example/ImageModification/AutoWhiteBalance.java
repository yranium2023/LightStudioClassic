package org.example.ImageModification;

/**
 * @author 申雄全
 * @Description 该类实现自动白平衡
 * @date 2023/12/9 14:56
 */
import io.vproxy.vfx.ui.button.ImageButton;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.ImageTools.ImageTransfer;
import org.example.Obj.ImageObj;

import java.awt.image.BufferedImage;
import java.util.concurrent.*;


public class AutoWhiteBalance {
    private ImageView imageView;
    private BufferedImage bufferedImage;
    private BufferedImage processedImage;//处理完的图形
    private double  redGain,greenGain,blueGain;

//    @Override
//    public void start(Stage  primaryStage)  {
//        Image originalImage = new Image(getClass().getResource("/image/c.jpg").toString());
//        imageView = new ImageView(originalImage);
//        imageView.setFitWidth(400);
//        imageView.setFitHeight(300);
//        bufferedImage = ImageTransfer.toBufferedImage(imageView.getImage());
//        processedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        VBox root = new VBox(10);
//        Button button=new Button("白平衡");
//        button.setOnAction(e->{
//            calculateRGBGain();
//            WhiteBalance();
//        });
//        root.getChildren().addAll(imageView,button);
//        Scene scene = new Scene(root, 600, 400);
//        primaryStage.setTitle("Image Exposure Adjustment");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//    }
    /**
     * @Description  该方法用于实现按钮对图片进行自动白平衡调整
     * @param editingImageObj
     * @author 吴鹄远
     * @date 2023/12/11 15:26
    **/

    public void autoWhiteBalance(ImageObj editingImageObj){
        bufferedImage=ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
        processedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        calculateRGBGain();
        WhiteBalance(editingImageObj);
    }

    private void calculateRGBGain(){
        // 计算图像的红色、绿色和蓝色通道的平均值
        double totalRed = 0.0, totalGreen = 0.0, totalBlue = 0.0;
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int totalPixels = width * height;
        // 遍历图像的每个像素
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = bufferedImage.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;
                // 累加每个通道的值
                totalRed += red;
                totalGreen += green;
                totalBlue += blue;
            }
        }
        // 计算平均值
        double averageRed = totalRed / totalPixels;
        double averageGreen = totalGreen / totalPixels;
        double averageBlue = totalBlue / totalPixels;
        // 计算平均灰度值
        double grayValue = (averageRed + averageGreen + averageBlue) / 3.0;
        // 计算红色、绿色和蓝色通道的增益
        redGain = grayValue / averageRed;
        greenGain = grayValue / averageGreen;
        blueGain = grayValue / averageBlue;
    }

    private void WhiteBalance(ImageObj editingImageObj){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            ForkJoinPool forkJoinPool=new ForkJoinPool();
            forkJoinPool.invoke(new AutoWhiteBalanceTask(0,0,bufferedImage.getWidth(),bufferedImage.getHeight()));
            forkJoinPool.shutdown();
            javafx.application.Platform.runLater(() -> {
                Image adjustedImage = SwingFXUtils.toFXImage(processedImage, null);
                //设置新图像
                editingImageObj.getEditImages().add(adjustedImage);
                editingImageObj.renewAll(adjustedImage);
                // 释放资源
                bufferedImage.flush();
                processedImage.flush();
            });
        });
        executor.shutdown();
    }
    class AutoWhiteBalanceTask extends RecursiveAction{
        private static final int Max =200000;
        private final int startX, startY, endX, endY;
        AutoWhiteBalanceTask(int startX, int startY, int endX, int endY){
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
                    int red = (int) (((rgb >> 16) & 0xFF) * redGain);
                    int green = (int) (((rgb >> 8) & 0xFF) * greenGain);
                    int blue = (int) ((rgb & 0xFF) * blueGain);
                    // Clamp the values to the valid range [0, 255]
                    red = Math.min(255, Math.max(0, red));
                    green = Math.min(255, Math.max(0, green));
                    blue = Math.min(255, Math.max(0, blue));
                    // 更新调整后的颜色值
                    int adjustedRGB = (alpha << 24)|(red << 16) | (green << 8) | blue;
                    processedImage.setRGB(x, y, adjustedRGB);
                   }
                }
            }else{
                int midX=(startX+endX)/2;
                int midY=(startY+endY)/2;
                ForkJoinTask<Void> A=new AutoWhiteBalanceTask(startX, startY, midX, midY).fork();
                ForkJoinTask<Void> B=new AutoWhiteBalanceTask(midX, startY, endX, midY).fork();
                ForkJoinTask<Void> C=new AutoWhiteBalanceTask(startX, midY, midX, endY).fork();
                ForkJoinTask<Void>  D=new AutoWhiteBalanceTask(midX, midY, endX, endY).fork();
                A.join();B.join();C.join();D.join();
            }
        }
    }
}

