package mobileimageprocessing.com.mobileimageprocessing;

import android.graphics.Color;

/**
 * Created by arthurkam on 2015-11-21.
 */

public class PixelateImageProcessingActivity extends BaseImageProcessingActivity {
    private static final int X_BOX = 12;
    private static final int Y_BOX = 12;

    @Override
    public int[][] processImageSequential(int[][] image) {
        for (int i = 0; i < image.length; i += X_BOX) {
            int xBound = i + X_BOX > image.length ? image.length : i + X_BOX;
            for (int j = 0; j < image[i].length/3; j += Y_BOX) {
                int redSum = 0;
                int greenSum = 0;
                int blueSum = 0;
                int yBound = j + Y_BOX > image[0].length/3 ? image[0].length/3 : j + Y_BOX;
                int counter = 0;
                for (int x = i; x < xBound; x++) {
                    for (int y = j; y < yBound; y++) {
                        redSum += Color.red(image[x][y]);
                        greenSum += Color.green(image[x][y]);
                        blueSum += Color.blue(image[x][y]);
                        counter++;
                    }
                }
                redSum/=counter;
                greenSum/=counter;
                blueSum/=counter;

                for (int x = i; x < xBound; x++) {
                    for (int y = j; y < yBound; y++) {
                        image[x][y] = Color.argb(255, redSum, greenSum, blueSum);
                    }
                }

            }
        }
        return image;
    }
}
