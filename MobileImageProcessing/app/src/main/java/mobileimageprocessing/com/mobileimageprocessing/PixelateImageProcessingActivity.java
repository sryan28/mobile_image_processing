package mobileimageprocessing.com.mobileimageprocessing;

import android.graphics.Color;

/**
 * Created by arthurkam on 2015-11-21.
 */

public class PixelateImageProcessingActivity extends BaseImageProcessingActivity {
    private static final int X_BOX = 100;
    private static final int Y_BOX = 100;

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
        // remaining part that will be fitted into the the last thread
        int xRemainder = image.length % (X_BOX * threadCount);
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
            threads[i].run();
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
    public int[][] processImagePipes(int[][] image) {
        return image;
    }
}
