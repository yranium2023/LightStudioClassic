package org.example.ImageModification;

import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 *  该类实现图片算法处理多线程
 * @author 申雄全
 * Date 2023/12/11 21:52
 */
public abstract class ThreadProcess {

    private final BufferedImage originalImage;
    private final BufferedImage processedImage;

    public ThreadProcess(BufferedImage originalImage,BufferedImage processedImage){
      this.originalImage=originalImage;
      this.processedImage=processedImage;
    }
    /**
     *  该方法实现对rgb值的具体算法，不同调整类型的具体算法不同
     * @param rgb
     * @return int
     * @author 申雄全
     * Date 2023/12/23 22:45
     */
    public abstract int calculateRGB(int rgb);

    /**
     *  该方法调用fork/join框架处理图片
     * @author 申雄全
     * Date 2023/12/23 23:27
     */
    public void run() {

            ForkJoinPool forkJoinPool = new ForkJoinPool();
            forkJoinPool.invoke(new Task(0, 0, originalImage.getWidth(), originalImage.getHeight()));
            originalImage.flush();
            processedImage.flush();
            forkJoinPool.shutdown();
    }
    /**
     *  该类是任务类，实现具体的fork/join框架
     * @author 申雄全
     * Date 2023/12/23 23:28
     */
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
                    new Task(startX, startY, midX, midY),
                    new Task(midX, startY, endX, midY),
                    new Task(startX, midY, midX, endY),
                    new Task(midX, midY, endX, endY)
                );
            }
        }
    }
}

