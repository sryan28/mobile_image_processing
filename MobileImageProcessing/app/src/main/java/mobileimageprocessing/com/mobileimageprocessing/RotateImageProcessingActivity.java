package mobileimageprocessing.com.mobileimageprocessing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RotateImageProcessingActivity extends BaseImageProcessingActivity {

    public static int[][] rotateBy(int angle, int[][] image, int start, int threadCount) {
        if (angle == 90) {
            return rotateImageClockWise(image, image[0].length, image.length, start, threadCount);
        } else if (angle == 180) {
            return rotateImageOneEighty(image, image[0].length, image.length, start, threadCount);
        } else {
            return rotateImageCounterClockWise(image, image[0].length, image.length, start, threadCount);
        }
    }

    @Override
    public int[][] processImageSequential(int[][] image) {
        return rotateBy(angle, image, 0, 1);
    }

    @Override
    public int[][] processImageThreads(int[][] image, int threadCount) {
        Callable<int[][]> callable;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Callable<int[][]>> tasks = new ArrayList<Callable<int[][]>>();
        List<Future<int[][]>> list;
        int[][] newImage;
        if (angle == 180) {
            newImage = new int[image.length][image[0].length];
        } else {
            newImage = new int[image[0].length][image.length];
        }
        for (int i = 0; i < threadCount; i++) {
            callable = new RotateCallable(image, image[0].length, image.length, i, threadCount);
            tasks.add(callable);
        }

        try {
            list = executorService.invokeAll(tasks);
            for (int i = 0; i < list.size(); i++) {
                if (angle == 180) {
                    System.arraycopy(list.get(i).get(), 0, newImage, i * (image.length / threadCount), image.length/threadCount);
                } else {
                    System.arraycopy(list.get(i).get(), 0, newImage, i * (image[0].length / threadCount), (image[0].length / threadCount));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return newImage;
    }


    public static int[][] rotateImageOneEighty(int[][] image, int height, int width, int start, int threadCount) {
        int newWidth = width / threadCount;
        int startColumn = start * newWidth;

        int[][] newImage = new int[newWidth][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < newWidth; j++) {
                newImage[j][i] = image[width - 1 - j - startColumn][height - 1 - i];
            }
        }

        return newImage;
    }

    public static int[][] rotateImageClockWise(int[][] image, int height, int width, int start, int threadCount) {
        int newHeight = width;
        int newWidth = height / threadCount;
        int startColumn = start * newWidth;

        int[][] newImage = new int[newWidth][newHeight];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newImage[j][i] = image[i][-startColumn + height - 1 - j];
            }
        }

        return newImage;
    }

    public static int[][] rotateImageCounterClockWise(int[][] image, int height, int width, int start, int threadCount) {
        int newHeight = width;
        int newWidth = height / threadCount;
        int startColumn = start * newWidth;

        int[][] newImage = new int[newWidth][newHeight];

        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                newImage[j][i] = image[width - 1 - i][startColumn + j];
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
            return rotateBy(angle, image, start, threadCount);
        }
    }

    @Override
    public int[][] processImageLooper(int[][] image, int threadCount) {
        int threadCounter = 0;
        LooperThread[] threads = new LooperThread[THREAD_COUNT];

        int[][] newImage;
        if (angle == 180) {
            newImage = new int[image.length][image[0].length];
        } else {
            newImage = new int[image[0].length][image.length];
        }
        int height = image[0].length;
        int width = image.length;

        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new LooperThread();
            threads[i].start();
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            // Ensure the mHandler is existing or we get NPEs
            threads[i].waitForLooper();
        }

        for (int i = 0; i < height; i++) {
            threads[threadCounter].addJob(new RotateLooperRunnable(image, i, height, width, newImage));
            threadCounter++;
            threadCounter %= THREAD_COUNT;
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i].exitLooperSafely();
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done!");
        return newImage;
    }

    private class RotateLooperRunnable implements Runnable {

        int[][] image;
        int width;
        int height;
        int[][] newImage;
        int i;

        public RotateLooperRunnable(int[][] image, int i, int height, int width, int[][] newImage) {
            this.image = image;
            this.newImage = newImage;
            this.i = i;
            this.width = width;
            this.height = height;
        }

        @Override
        public void run() {
            if (angle == 90) {
                for (int j = 0; j < width; j++) {
                    newImage[i][j] = image[j][height - 1 - i];
                }
            } else if (angle == 180) {
                for (int j = 0; j < width; j++) {
                    newImage[j][i] = image[width-1-j][height-1-i];
                }
            } else {
                for (int j = 0; j < width; j++) {
                    newImage[i][j] = image[width - 1 - j][i];
                }
            }
        }
    }


    private class LooperThread extends Thread {
        public Handler mHandler;

        @Override
        public void run() {
            Looper.prepare();
            synchronized (this) {
                mHandler = new Handler();
                notifyAll();
            }
            Looper.loop();
        }

        public void addJob(Runnable r) {
            this.mHandler.post(r);
        }

        public void exitLooperSafely() {
            this.mHandler.getLooper().quitSafely();
        }

        public void waitForLooper() {
            synchronized (this) {
                while (mHandler == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

}
