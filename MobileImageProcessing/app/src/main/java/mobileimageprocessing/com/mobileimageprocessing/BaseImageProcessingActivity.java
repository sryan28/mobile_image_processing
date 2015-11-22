package mobileimageprocessing.com.mobileimageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseImageProcessingActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "mobileimageprocessing.com.mobileimageprocessing.imageResult";
    private long[] times;
    public static final int THREAD_COUNT = 4;
    public int threads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        times = new long[3];
        Bitmap bitmap = MainActivity.bitmap;

//        Bundle extras = getIntent().getExtras();
//        threads = extras.getInt("threadsCount");

        int[][] seqRes = processImageSequentialTimed(arrayFromBitmap(bitmap));
        processImagePipesTimed(arrayFromBitmap(bitmap));
        processImageThreadsTimed(arrayFromBitmap(bitmap));
        Bitmap bitmapOutput = bitmapFromArray(seqRes);

        Intent output = new Intent();
        MainActivity.input = bitmapOutput;
        setResult(RESULT_OK, output);
        String toastMsg = String.format("Sequential: %d\nThreads: %d\nPipes: %d", times[0], times[1], times[2]);
        System.out.println(toastMsg);
        Toast toast = Toast.makeText(this.getApplicationContext(), toastMsg, Toast.LENGTH_LONG);
        toast.show();
        finish();

    }

    private int[][] processImageSequentialTimed(int[][] image) {
        long startTime = System.currentTimeMillis();
        int[][] out = processImageSequential(image);
        long endTime = System.currentTimeMillis();
        times[0] = endTime - startTime;
        return out;

    }

    public int[][] processImageSequential(int[][] image) {
        for (int j = 0; j < image.length / 2; j++) {
            for (int i = 0; i < image[0].length; i++) {
                image[j][i] = 0;
            }
        }
        return image;
    }

    private int[][] processImageThreadsTimed(int[][] image) {
        long startTime = System.currentTimeMillis();
        int[][] out = processImageThreads(image, THREAD_COUNT);
        long endTime = System.currentTimeMillis();
        times[1] = endTime - startTime;
        return out;

    }

    public int[][] processImageThreads(int[][] image, int threads) {
        for (int j = 0; j < image.length / 2; j++) {
            for (int i = 0; i < image[0].length; i++) {
                image[j][i] = 0;
            }
        }
        return image;
    }

    private int[][] processImagePipesTimed(int[][] image) {
        long startTime = System.currentTimeMillis();
        int[][] out = processImagePipes(image);
        long endTime = System.currentTimeMillis();
        times[2] = endTime - startTime;
        return out;

    }

    public int[][] processImagePipes(int[][] image) {
        for (int j = 0; j < image.length / 2; j++) {
            for (int i = 0; i < image[0].length; i++) {
                image[j][i] = 0;
            }
        }
        return image;
    }

    public static Bitmap bitmapFromArray(int[][] pixels2d) {
        int width = pixels2d.length;
        int height = pixels2d[0].length;
        int[] pixels = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i+width*j] = pixels2d[i][j];
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public static int[][] arrayFromBitmap(Bitmap source) {
        long startTime = System.currentTimeMillis();
        int width = source.getWidth();
        int height = source.getHeight();
        int[][] result = new int[width][height];
        int[] pixels = new int[width * height];
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                result[i][j] = pixels[i+width*j];
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("a from b: " + (endTime - startTime));
        return result;
    }


}
