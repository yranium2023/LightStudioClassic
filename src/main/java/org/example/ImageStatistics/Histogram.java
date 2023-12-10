package org.example.ImageStatistics;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.ImageTools.ImageTransfer;
import org.example.Main;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @author 申雄全
 * @Description:该类用于统计实现灰度直方图和rgb直方图
 * @date 2023/12/5 17:58
 */

public class Histogram{
    private static final int NUM_BINS = 256; // 灰度级别的数量
    private static int[] histogram = new int[NUM_BINS];
    private static int[] redHistogram = new int[NUM_BINS];
    private static int[] greenHistogram = new int[NUM_BINS];
    private static int[] blueHistogram = new int[NUM_BINS];
    private static AreaChart<Number, Number> Histogram;//直方图，统计图片灰度和R,G,B
    private static BufferedImage bufferedImage;
    public static void drawHistogram(Image image, Pane histogramPane){
        //清空
        histogramPane.getChildren().clear();
        //读取图像
        bufferedImage=ImageTransfer.toBufferedImage(image);
        // 创建灰度直方图
        Histogram= createHistogram(image);
        Histogram.setCreateSymbols(false);
        Histogram.setVerticalGridLinesVisible(false);
        Histogram.setHorizontalGridLinesVisible(false);
        Histogram.setLegendVisible(false);
        Histogram.prefWidthProperty().bind(histogramPane.widthProperty());
        Histogram.prefHeightProperty().bind(histogramPane.heightProperty());
        //将直方图加入pane中
        histogramPane.getChildren().add(Histogram);
        System.out.println("创建直方图成功");
    }

    private static AreaChart<Number, Number> createHistogram(Image image) {
        // 创建灰度直方图的数据系列
        XYChart.Series<Number, Number> grayseries = new XYChart.Series<>();
        XYChart.Series<Number, Number> redSeries = new XYChart.Series<>();
        redSeries.setName("Red");
        XYChart.Series<Number, Number> greenSeries = new XYChart.Series<>();
        greenSeries.setName("Green");
        XYChart.Series<Number, Number> blueSeries = new XYChart.Series<>();
        blueSeries.setName("Blue");
        // 添加灰度直方图的数据系列到图表中
        // 设置灰度直方图的X轴和Y轴
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setOpacity(0);
        yAxis.setOpacity(0);

        AreaChart<Number, Number> histogram1 = new AreaChart<>(xAxis, yAxis);

        // 遍历图像的像素并统计灰度直方图

            grayCount(image);
            for (int i = 0; i < NUM_BINS; i++) {
                grayseries.getData().add(new XYChart.Data<>(i, histogram[i]));
                redSeries.getData().add(new XYChart.Data<>(i,redHistogram[i]));
                greenSeries.getData().add(new XYChart.Data<>(i,greenHistogram[i]));
                blueSeries.getData().add(new XYChart.Data<>(i,blueHistogram[i]));
            }
            histogram1.getData().addAll(grayseries,redSeries,greenSeries,blueSeries);
        Arrays.fill(histogram,0);
        Arrays.fill(redHistogram,0);
        Arrays.fill(greenHistogram,0);
        Arrays.fill(blueHistogram,0);
        return histogram1;
    }

    private static void grayCount(Image image){


        // 使用ForkJoinPool创建一个线程池
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // 提交任务给线程池
        forkJoinPool.invoke(new ParallelHistogramTask( 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight()));
        forkJoinPool.shutdown();

    }
    /**
     * @describle 实现ParallelHistogramTask内部方法
     * @author 申雄全
     * @updateTime 2023/12/5 21:29
     */
    static class ParallelHistogramTask extends RecursiveAction{
        private static final int Max =50000;
        private final int startX, startY, endX, endY;

        private ParallelHistogramTask( int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        @Override
        protected void compute() {
            // 在这里实现对图像的处理逻辑，例如滤波、变换等
            if((endX-startX)*(endY-startY)<Max) {
                for (int x = startX; x < endX; x++) {
                    for (int y = startY; y < endY; y++) {
                        // 获取源图像的颜色信息
                        int rgb = bufferedImage.getRGB(x, y);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                        int gray = (int) (0.299*r+0.587*g+0.114*b); // 灰度值的计算
                        histogram[gray]++;
                        redHistogram[r]++;
                        greenHistogram[g]++;
                        blueHistogram[b]++;
                    }
                }
            }else{
                int midX = (startX + endX) / 2;
                int midY = (startY + endY) / 2;
                invokeAll(
                        new ParallelHistogramTask(startX, startY, midX, midY),
                        new ParallelHistogramTask(midX, startY, endX, midY),
                        new ParallelHistogramTask(startX, midY, midX, endY),
                        new ParallelHistogramTask(midX, midY, endX, endY)
                );
            }
        }

    }
}

