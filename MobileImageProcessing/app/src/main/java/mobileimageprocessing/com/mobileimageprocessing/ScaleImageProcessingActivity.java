package mobileimageprocessing.com.mobileimageprocessing;

/**
 * Created by user on 11/21/2015.
 */
public class ScaleImageProcessingActivity extends BaseImageProcessingActivity{

    @Override
    public int[][] processImageSequential(int[][] image ) {
        return scaleImage(image, image.length, image[0].length);
    }

    public int[][] scaleImage(int[][] image, int width, int height) {
        int newWidth = width;
        int newHeight = height;

        int origHeight = image[0].length;
        int origWidth = image.length;

        int [][] temp = new int[origWidth][origHeight];

        double xratio = (double)origWidth / (double)newWidth;
        double yratio = (double)origHeight/ (double)newHeight;

        double px, py;
        for(int i = 0; i < newHeight; i++) {
            for(int j = 0; j < newWidth; j++) {
                px = Math.floor(j * xratio);
                py = Math.floor(i * yratio);
                temp[i][(i*newWidth) + j] = image[i][(int) ((py * origWidth) + px)];
            }
        }
        return temp;
    }
}
