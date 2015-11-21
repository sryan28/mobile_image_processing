package mobileimageprocessing.com.mobileimageprocessing;

import android.graphics.Color;

/**
 * Created by user on 11/21/2015.
 */
public class ScaleImageProcessingActivity extends BaseImageProcessingActivity{

    @Override
    public int[][] processImageSequential(int[][] image ) {
        return scaleImagePixelate(image, (int) (image.length), (int) (image[0].length));
    }

    public int[][] scaleImagePixelate(int [][] image, int width, int height){
        System.out.println("pixeal scale called");
        double scale = 0.5;
        int X_BOX = (int)(1/scale);
        int Y_BOX = (int)(1/scale);
        System.out.println(X_BOX);
        System.out.println(Y_BOX);

        int[][] image2 = new int[image.length/2][image[0].length/2];
        int xCounter=0;
        int yCounter=0;
        for (int i = 0; i < image.length; i += X_BOX) {
            int xBound = i + X_BOX > image.length ? image.length : i + X_BOX;
            yCounter=0;
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
                redSum/=counter;
                greenSum/=counter;
                blueSum/=counter;

                image2[xCounter][yCounter] = Color.argb(255,redSum,greenSum,blueSum);
                yCounter++;
            }
            xCounter++;
        }

        return image2;
    }
    public int[][] scaleImage(int[][] image, int width, int height) {
        int newWidth = width;
        int newHeight = height;

        int [][] result = new int[newWidth][newHeight];

        int origHeight = image[0].length;
        int origWidth = image.length;

        int [] temp = new int[ origWidth *  origHeight];
        int [] newtmp = new int[(newWidth * newHeight)];

        int k = 0;
        for(int i = 0; i < origWidth; i++) {
            for(int j = 0; j < origHeight; j++) {
                temp[k] = image[i][j];
                k++;
            }
        }

        double xratio = (double)origWidth / (double)newWidth;
        double yratio = (double)origHeight/ (double)newHeight;

        //SCALING DONE HERE
        double px, py;
        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newWidth; j++) {
                px = Math.floor(j * xratio);
                py = Math.floor(i * yratio);
                newtmp[( i * newWidth) + j] = temp[(int) ((py * origWidth) + px)];
            }
        }
        //CONVERT BACK TO 2D
        //int x = 0;
        for(int i = 0, x = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
                result[i][j] = newtmp[x++];
            }
        }
        return result;
    }
}
