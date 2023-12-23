package org.example.Curve.SplineCanvas;


import io.vproxy.vfx.util.FXUtils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import org.example.ImageModification.ImageAdjustment;
import org.example.ImageModification.ThreadProcess;
import org.example.ImageTools.ImageTransfer;
import org.example.Obj.ImageObj;
import org.example.Scene.ImageEditScene;
import org.example.StaticValues;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

/**
 * @author 申雄全
 * @Description 该类实现曲线调整后图像效果的计算
 * @date 2023/12/22 22:46
 */
public class SplineBrightnessAdjustment extends ImageAdjustment {

    private static Rectangle curveRec=new Rectangle(190,190){{
        setStrokeWidth(2);
        setStroke(Color.WHITE);
        setFill(Color.TRANSPARENT);
        setStrokeType(StrokeType.INSIDE);
    }};
    /**
     * @Description  这个类用于创建曲线
     * @param curvePane
     * @param editingImageObj
     * @author 吴鹄远
     * @date 2023/12/14 10:27
    **/
    public static void addCurve(Pane curvePane, ImageObj editingImageObj){
        if(editingImageObj!=null){
            bufferedImage=ImageTransfer.toBufferedImage(editingImageObj.getEditingImage());
            ImageAdjustment.setProcessedImage();
            StackPane stackPane=new StackPane();
            stackPane.getChildren().addAll(curveRec,editingImageObj.getSplineCanvas());
            curvePane.getChildren().add(stackPane);
            FXUtils.observeWidthCenter(curvePane,stackPane);
        }
    }
    /**
     * @Description 调用LUT对图片进行修改
     * @author 申雄全，吴鹄远
     * @date 2023/12/23 23:35
     */
    public static void applyLUTToImage(){

        SplineCanvas.ResultLUT.checkLUT();
        ForkJoinPool forkJoinPool=new ForkJoinPool();
        forkJoinPool.invoke(new LUTTask(0,0,bufferedImage.getWidth(),bufferedImage.getHeight(),SplineCanvas.ResultLUT));
        bufferedImage.flush();
        processedImage.flush();
        forkJoinPool.shutdown();
    }


        /**
        * @Description 该类实现fork/join框架实现算法调整rgb
        * @author 申雄全
        * @date 2023/12/23 23:36
        */
    static class LUTTask extends RecursiveAction{
        private static final int Max =250000;
        private final int startX, startY, endX, endY;
        private final LUT L;
        LUTTask(int startX, int startY, int endX, int endY,LUT L){
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.L=L;
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
                        if(L!=null){
                            red=L.getY(red);
                            green=L.getY(green);
                            blue=L.getY(blue);
                        }
                        // Compose the adjusted color
                        int adjustedRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                        // Set the adjusted color to the pixel
                        processedImage.setRGB(x, y, adjustedRgb);
                    }
                }
            }else{
                int midX=(startX+endX)>>1;
                int midY=(startY+endY)>>1;
                ForkJoinTask<Void> A=new LUTTask(startX, startY, midX, midY,L).fork();
                ForkJoinTask<Void> B=new LUTTask(midX, startY, endX, midY,L).fork();
                ForkJoinTask<Void> C=new LUTTask(startX, midY, midX, endY,L).fork();
                ForkJoinTask<Void>  D=new LUTTask(midX, midY, endX, endY,L).fork();
                A.join();B.join();C.join();D.join();
            }
        }
    }
}
