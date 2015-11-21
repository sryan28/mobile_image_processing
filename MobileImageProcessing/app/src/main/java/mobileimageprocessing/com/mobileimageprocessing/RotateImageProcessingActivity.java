package mobileimageprocessing.com.mobileimageprocessing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RotateImageProcessingActivity extends BaseImageProcessingActivity {

    @Override
    public int[][] processImageSequential(int[][] image) {
        return rotateImageClockwise(image, image[0].length, image.length);
    }

    public int[][] rotateImageClockwise(int[][] image, int height, int width){

        int[][] newImage = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newImage[i][j] = image[i][j];
                System.out.println(newImage[i][j]);
            }
            System.out.println("\n");

        }

        return newImage;
    }
}
