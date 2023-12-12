package org.example.ImageModification;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * @author 申雄全
 * @Description
 * @date 2023/12/11 21:52
 */
public abstract class ThreadProcess {

    private final BufferedImage originalImage;
    private final BufferedImage processedImage;//处理完的图形

    public ThreadProcess(BufferedImage originalImage,BufferedImage processedImage){
      this.originalImage=originalImage;
      this.processedImage=processedImage;
    }
    public abstract int calculateRGB(int rgb);

    public void run() {
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            forkJoinPool.invoke(new Task(0, 0, originalImage.getWidth(), originalImage.getHeight()));
            originalImage.flush();
            processedImage.flush();
            forkJoinPool.shutdown();
    }
    class Task extends RecursiveAction {
        private static final int Max = 250000;

        private final int startX, startY, endX, endY;
        Task(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }
        protected void compute() {
            if ((endX - startX) * (endY - startY) < Max) {
                for (int x = startX; x < endX; x++) {
                    for (int y = startY; y < endY; y++) {
                        int adjustedRgb=calculateRGB(originalImage.getRGB(x, y));
                        processedImage.setRGB(x, y, adjustedRgb);
                    }
                }
            } else {
                int midX = (startX + endX) >>1;
                int midY = (startY + endY) >>1;
                invokeAll(
                    new Task(startX, startY, midX, midY).fork(),
                    new Task(midX, startY, endX, midY).fork(),
                    new Task(startX, midY, midX, endY).fork(),
                    new Task(midX, midY, endX, endY).fork()
                );
            }
        }
    }
}

