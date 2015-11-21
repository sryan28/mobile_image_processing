package mobileimageprocessing.com.mobileimageprocessing;

/**
 * Created by user on 11/21/2015.
 */
public class ScaleImageProcessingActivity extends BaseImageProcessingActivity{

    @Override
    public int[][] processImageSequential(int[][] image ) {
        return scaleImage(image, (int) (image.length * 0.5), (int) (image[0].length * 0.5));
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
