package mobileimageprocessing.com.mobileimageprocessing;

import android.graphics.Color;
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

    @Override
    public int[][] processImageSequential(int[][] image) {
        return rotateImageOneEighty(image, image[0].length, image.length, 0, 1);
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
            return RotateImageProcessingActivity.rotateImageOneEighty(image, height, width, start, threadCount);
        }
    }

    private class RotateRunnable implements Runnable {
        int[][] image;
        int start;
        int width;
        int height;
        int threadCount;
        int[][] newImage;

        public RotateRunnable(int[][] image, int height, int width, int start, int threadCount, int[][] newImage) {
            this.image = image;
            this.width = width;
            this.height = height;
            this.start = start;
            this.threadCount = threadCount;
            this.newImage = newImage;
        }

        @Override
        public void run() {
            System.arraycopy(RotateImageProcessingActivity.rotateImageOneEighty(image, height, width, start, threadCount), 0, newImage, start * (image.length / threadCount), (image.length / threadCount));
        }
    }


//    @Override
//    public int[][] processImagePipes(int[][] image, int threadCount) {
//        Thread[] threads = new Thread[threadCount];
//        int[][] newImage = new int[image.length][image[0].length];
//
//        for (int i = 0; i < threadCount; i++) {
//            threads[i] = new Thread(new RotateRunnable(image, image[0].length, image.length, i, threadCount, newImage));
//            threads[i].start();
//        }
//
//        for (int i = 0; i < threadCount; i++) {
//            try {
//                threads[i].join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return newImage;
//    }

    @Override
    public int[][] processImageLooper(int[][] image, int threadCount) {
        int threadCounter = 0;
        LooperThread[] threads = new LooperThread[THREAD_COUNT];

        int[][] newImage = new int[image.length][image[0].length];
        int height = image[0].length;
        int width = image.length;
        int newWidth = image.length/THREAD_COUNT;

        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new LooperThread();
            threads[i].start();
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            // Ensure the mHandler is existing or we get NPEs
            threads[i].waitForLooper();
        }

//        for (int i = 0; i < height; i++) {
//            for (int j = 0; j < width; j++) {
//                threads[threadCounter].addJob(new RotateLooperRunnable(image, i,j, height, width, newImage));
//                threadCounter++;
//                threadCounter %= THREAD_COUNT;
//            }
//        }

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
            for (int j = 0; j < width; j++) {
                newImage[j][i] = image[width-1-j][height-1-i];
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
                System.out.println("Handler created");
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
