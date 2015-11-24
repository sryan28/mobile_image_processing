package mobileimageprocessing.com.mobileimageprocessing;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by arthurkam on 2015-11-21.
 */

public class PixelateImageProcessingActivity extends BaseImageProcessingActivity {
    private static final int X_BOX = 120;
    private static final int Y_BOX = 120;

    @Override
    public int[][] processImageSequential(int[][] image) {
        for (int i = 0; i < image.length; i += X_BOX) {
            int xBound = i + X_BOX > image.length ? image.length : i + X_BOX;
            for (int j = 0; j < image[i].length; j += Y_BOX) {
                int redSum = 0;
                int greenSum = 0;
                int blueSum = 0;
                int yBound = j + Y_BOX > image[0].length ? image[0].length : j + Y_BOX;
                int counter = 0;
                for (int x = i; x < xBound; x++) {
                    for (int y = j; y < yBound; y++) {
                        redSum += Color.red(image[x][y]);
                        greenSum += Color.green(image[x][y]);
                        blueSum += Color.blue(image[x][y]);
                        counter++;
                    }
                }
                redSum /= counter;
                greenSum /= counter;
                blueSum /= counter;

                for (int x = i; x < xBound; x++) {
                    for (int y = j; y < yBound; y++) {
                        image[x][y] = Color.argb(255, redSum, greenSum, blueSum);
                    }
                }

            }
        }
        return image;
    }

    @Override
    public int[][] processImageThreads(int[][] image, int threadCount) {
        Thread[] threads = new Thread[threadCount];
        int chunks = image.length / (X_BOX);
        int chunksPerThread = chunks / threadCount;
        // Extra chunks to give out to the first n threads
        int extraChunks = chunks % threadCount;
        int previousEnd = 0;
        for (int i = 0; i < threadCount; i++) {
            int start = previousEnd;
            int end = start + chunksPerThread * X_BOX;
            if (extraChunks != 0) {
                end += X_BOX;
                extraChunks--;
            }
            if (i == threadCount - 1) {
                end = image.length;
            }
            previousEnd = end;
            threads[i] = new Thread(new PixelateRunnable(image, start, end, 0, image[0].length));
            threads[i].start();
        }
        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return image;
    }

    private class PixelateRunnable implements Runnable {
        int[][] image;
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;

        PixelateRunnable(int[][] image, int xStart, int xEnd, int yStart, int yEnd) {
            this.image = image;
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
        }

        @Override
        public void run() {
            for (int i = xStart; i < xEnd; i += X_BOX) {
                int xBound = i + X_BOX > xEnd ? xEnd : i + X_BOX;
                for (int j = yStart; j < yEnd; j += Y_BOX) {
                    int redSum = 0;
                    int greenSum = 0;
                    int blueSum = 0;
                    int yBound = j + Y_BOX > yEnd ? yEnd : j + Y_BOX;
                    int counter = 0;
                    for (int x = i; x < xBound; x++) {
                        for (int y = j; y < yBound; y++) {
                            redSum += Color.red(image[x][y]);
                            greenSum += Color.green(image[x][y]);
                            blueSum += Color.blue(image[x][y]);
                            counter++;
                        }
                    }
                    redSum /= counter;
                    greenSum /= counter;
                    blueSum /= counter;

                    for (int x = i; x < xBound; x++) {
                        for (int y = j; y < yBound; y++) {
                            image[x][y] = Color.argb(255, redSum, greenSum, blueSum);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public int[][] processImageLooper(int[][] image, int threadCount) {
        int threadCounter = 0;
        LooperThread[] threads = new LooperThread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new LooperThread();
            threads[i].start();
        }
        //return image;

        for (int i = 0; i < THREAD_COUNT; i++) {
            // Ensure the mHandler is existing or we get NPEs
            threads[i].waitForLooper();
        }
        for (int i = 0; i < image.length; i += X_BOX) {
//            System.out.println(i);
            int xBound = i + X_BOX > image.length ? image.length : i + X_BOX;
            for (int j = 0; j < image[i].length; j += Y_BOX) {
                int yBound = j + Y_BOX > image[0].length ? image[0].length : j + Y_BOX;
                threads[threadCounter].addJob(new PixelateLooperRunnable(image, i, xBound, j, yBound));
                threadCounter++;
                threadCounter %= THREAD_COUNT;
            }
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
        return image;

    }

    private class LooperThread extends Thread {
        public Handler mHandler;

        public LooperThread() {

        }

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

    private class PixelateLooperRunnable implements Runnable {

        int[][] image;
        int xStart;
        int xEnd;
        int yStart;
        int yEnd;

        PixelateLooperRunnable(int[][] image, int xStart, int xEnd, int yStart, int yEnd) {
            this.image = image;
            this.xStart = xStart;
            this.xEnd = xEnd;
            this.yStart = yStart;
            this.yEnd = yEnd;
        }

        @Override
        public void run() {
            int redSum = 0;
            int greenSum = 0;
            int blueSum = 0;
            int counter = 0;
            for (int x = xStart; x < xEnd; x++) {
                for (int y = yStart; y < yEnd; y++) {
                    redSum += Color.red(image[x][y]);
                    greenSum += Color.green(image[x][y]);
                    blueSum += Color.blue(image[x][y]);
                    counter++;
                }
            }
            redSum /= counter;
            greenSum /= counter;
            blueSum /= counter;
            for (int x = xStart; x < xEnd; x++) {
                for (int y = yStart; y < yEnd; y++) {
                    image[x][y] = Color.argb(255, redSum, greenSum, blueSum);
                }
            }
        }
    }
}

