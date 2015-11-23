package mobileimageprocessing.com.mobileimageprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RotateImageProcessingActivity extends BaseImageProcessingActivity {

    @Override
    public int[][] processImageSequential(int[][] image) {
        return rotateImageCounterClockWise(image, image[0].length, image.length, 0, 1);
    }

    @Override
    public int[][] processImageThreads(int[][] image, int threadCount) {
        Callable<int[][]> callable;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Callable<int[][]>> tasks = new ArrayList<Callable<int[][]>>();
        List<Future<int[][]>> list;

        int[][] newImage = new int[image.length][image[0].length];

        for (int i = 0; i < threadCount; i++) {
            callable = new RotateCallable(image, image[0].length, image.length, i, threadCount);
            tasks.add(callable);
        }

        try {
            list = executorService.invokeAll(tasks);
            for (int i = 0; i < list.size(); i++) {
                System.arraycopy(list.get(i).get(), 0, newImage, i * (image.length / threadCount), (image.length / threadCount));
//                System.arraycopy(list.get(i).get(), 0, newImage, i * (image[0].length / threadCount), image[0].length/threadCount);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return newImage;
    }

    public static int[][] rotateImageOneEighty(int[][] image, int height, int width, int start, int threadCount){
        int newWidth = width/threadCount;
        int startColumn = start*newWidth;

        int[][] newImage = new int[newWidth][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < newWidth; j++) {
                newImage[j][i] = image[width-1-j-startColumn][height-1-i];
            }
        }

        return newImage;
    }

    public static int[][] rotateImageClockWise(int[][] image, int height, int width, int start, int threadCount){
        int newHeight = width;
        int newWidth = height/threadCount;
        int startColumn = start*newWidth;

        int[][] newImage = new int[newWidth][newHeight];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newImage[j][i] = image[i][-startColumn+height-1-j];
            }
        }

        return newImage;
    }

    public static int[][] rotateImageCounterClockWise(int[][] image, int height, int width, int start, int threadCount){
        int newHeight = width;
        int newWidth = height/threadCount;
        int startColumn = start*newWidth;

        int[][] newImage = new int[newWidth][newHeight];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newImage[j][i] = image[width-1-i][startColumn+j];
            }
        }

        return newImage;
    }

    private class RotateCallable implements Callable<int[][]> {
        int[][] image;
        int start;
        int width;
        int height;
        int threadCount;

        public RotateCallable(int[][] image, int height, int width, int start, int threadCount) {
            this.image = image;
            this.width = width;
            this.height = height;
            this.start = start;
            this.threadCount = threadCount;
        }

        @Override
        public int[][] call() throws Exception {
            return RotateImageProcessingActivity.rotateImageOneEighty(image, height, width, start, threadCount);
        }
    }
}
