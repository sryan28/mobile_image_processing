package mobileimageprocessing.com.mobileimageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class BaseImageProcessingActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "mobileimageprocessing.com.mobileimageprocessing.imageResult";
//    public static Bitmap bOutput;
    private long[] times;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("works2");
        super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_base_image_processing);
        Intent intent = getIntent();
        times = new long[3];
//        Bitmap bitmap = intent.getParcelableExtra(EXTRA_MESSAGE);
        Bitmap bitmap = MainActivity.bitmap;
        int[][] seqRes = processImageSequentialTimed(arrayFromBitmap(bitmap));
        processImagePipesTimed(arrayFromBitmap(bitmap));
        processImageThreadsTimed(arrayFromBitmap(bitmap));
        Bitmap bitmapOutput = bitmapFromArray(seqRes);

        Intent output = new Intent();
//        output.putExtra(this.EXTRA_MESSAGE, bitmapOutput);
        MainActivity.input = bitmapOutput;
        setResult(RESULT_OK, output);
        String toastMsg = String.format("Sequential: %d\nThreads: %d\nPipes: %d",times[0],times[1],times[2]);
        Toast toast = Toast.makeText(this.getApplicationContext(), toastMsg, Toast.LENGTH_LONG);
        toast.show();
        finish();

    }
    private int [][] processImageSequentialTimed(int[][] image){
        long startTime = System.currentTimeMillis();
        int[][] out = processImageSequential(image);
        long endTime   = System.currentTimeMillis();
        times[0] = endTime-startTime;
        return out;

    }
    public int[][] processImageSequential(int[][] image){
        for(int j=0;j<image.length/2;j++) {
            for (int i = 0; i < image[0].length; i++) {
                image[j][i] = 0;
            }
        }
        return image;
    }
    private int [][] processImageThreadsTimed(int[][] image){
        long startTime = System.currentTimeMillis();
        int[][] out = processImageThreads(image);
        long endTime   = System.currentTimeMillis();
        times[1] = endTime-startTime;
        return out;

    }
    public int[][] processImageThreads(int[][] image){
        for(int j=0;j<image.length/2;j++) {
            for (int i = 0; i < image[0].length; i++) {
                image[j][i] = 0;
            }
        }
        return image;
    }
    private int [][] processImagePipesTimed(int[][] image){
        long startTime = System.currentTimeMillis();
        int[][] out = processImagePipes(image);
        long endTime   = System.currentTimeMillis();
        times[2] = endTime-startTime;
        return out;

    }
    public int[][] processImagePipes(int[][] image){
        for(int j=0;j<image.length/2;j++) {
            for (int i = 0; i < image[0].length; i++) {
                image[j][i] = 0;
            }
        }
        return image;
    }

//    public static Bitmap bitmapFromArray(int[][] pixels2d){
//        int width = pixels2d.length;
//        int height = pixels2d[0].length;
//        int[] pixels = new int[width * height];
//        int pixelsIndex = 0;
//        for (int i = 0; i < width; i++)
//        {
//            for (int j = 0; j < height; j++)
//            {
//                pixels[pixelsIndex] = pixels2d[i][j];
//                pixelsIndex ++;
//            }
//        }
//        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
//    }
//
//    public static int[][] arrayFromBitmap(Bitmap source){
//        int width = source.getWidth();
//        int height = source.getHeight();
//        int[][] result = new int[width][height];
//        int[] pixels = new int[width*height];
//        source.getPixels(pixels, 0, width, 0, 0, width, height);
//        int pixelsIndex = 0;
//        for (int i = 0; i < width; i++)
//        {
//            for (int j = 0; j < height; j++)
//            {
//                result[i][j] =  pixels[pixelsIndex];
//                pixelsIndex++;
//            }
//        }
//        return result;
//    }

    public static Bitmap bitmapFromArray(int[][] array) {

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(array.length, array[0].length, conf);

        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                bmp.setPixel(i, j, array[i][j]);
            }
        }
        return bmp;
    }

    public static int[][] arrayFromBitmap(Bitmap map) {
        int width = map.getWidth();
        int height = map.getHeight();
        int[][] result = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                result[i][j] = map.getPixel(i, j);
            }
        }
        return result;

    }

}
