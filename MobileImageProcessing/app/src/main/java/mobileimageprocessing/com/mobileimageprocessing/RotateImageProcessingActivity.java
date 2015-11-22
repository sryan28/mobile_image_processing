package mobileimageprocessing.com.mobileimageprocessing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class RotateImageProcessingActivity extends BaseImageProcessingActivity {

    @Override
    public int[][] processImageSequential(int[][] image) {
        return rotateImageClockWise(image, image[0].length, image.length, 0);
    }

    public int[][] rotateImageOneEighty(int[][] image, int height, int width, int start){
        int[][] newImage = new int[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newImage[i][j] = image[width-1-j][height-1-j];
            }
        }

        return newImage;
    }

    @Override
    public int[][] processImageThreads(int[][] image, int threadCount) {
        Thread[] threads = new Thread[threadCount];
        int numRows = image.length/threadCount;
        System.out.println("numrow:" + numRows);

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new RotateRunnable(image, image[0].length, numRows, i*numRows));
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

    public static int[][] rotateImageClockWise(int[][] image, int height, int width, int start){
        int newHeight = width;
        int newWidth = height;

        int[][] newImage = new int[newWidth][newHeight];

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                newImage[i][j] = image[start+j][height-1-i];
            }
        }

        return newImage;
    }

    public int[][] rotateImageCounterClockWise(int[][] image, int height, int width){
        int newHeight = width;
        int newWidth = height;

        int[][] newImage = new int[newWidth][newHeight];

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                newImage[i][j] = image[width-1-j][i];
            }
        }

        return newImage;
    }

    private class RotateRunnable implements Runnable {
        int[][] image;
        int start;
        int width;
        int height;

        RotateRunnable(int[][] image, int height, int width,  int start) {
            this.image = image;
            this.width = width;
            this.height = height;
            this.start = start;
        }

        @Override
        public void run() {
            RotateImageProcessingActivity.rotateImageClockWise(image, height, width, start);
        }
    }
}
