package mobileimageprocessing.com.mobileimageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseImageProcessingActivity extends AppCompatActivity {
    private long[] times;
    public static int THREAD_COUNT;
    public static int angle;
    private final int TEST_ITERATIONS = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        THREAD_COUNT = intent.getIntExtra("threads", 2);
        angle = intent.getIntExtra("rotateBy", 90);
        boolean runHundredTimes = intent.getBooleanExtra("runHundredTimes", false);
        super.onCreate(savedInstanceState);
        if (runHundredTimes) {
            System.out.println("Running 100 times !");
            long[] totalTimes = new long[3];
            Bitmap bitmap = MainActivity.bitmap;
            int[][] image = arrayFromBitmap(bitmap);
            times = new long[3];
            int[][] seqRes = null;
            for (int i = 0; i < TEST_ITERATIONS; i++) {
                // Run the tests, add to results
                seqRes = processImageSequentialTimed(cloneInt2dArray(image));
                processImageThreadsTimed(cloneInt2dArray(image));
                processImageLooperTimed(cloneInt2dArray(image));
                for(int j=0;j<3;j++){
                    totalTimes[j]+=times[j];
                }
            }
            for(int j=0;j<3;j++){
                totalTimes[j]/=TEST_ITERATIONS;
                times[j] = totalTimes[j];
            }
            Bitmap bitmapOutput = bitmapFromArray(seqRes);
            MainActivity.input = bitmapOutput;
        } else {
            times = new long[3];
            Bitmap bitmap = MainActivity.bitmap;
            int[][] image = arrayFromBitmap(bitmap);
            int[][] seqRes = processImageSequentialTimed(cloneInt2dArray(image));
            int[][] threadRes = processImageThreadsTimed(cloneInt2dArray(image));
            int[][] looperRes = processImageLooperTimed(cloneInt2dArray(image));
            Bitmap bitmapOutput = bitmapFromArray(looperRes);
            MainActivity.input = bitmapOutput;

        }

        Intent output = new Intent();
        setResult(RESULT_OK, output);
        String toastMsg = String.format("Sequential: %d\nThreads: %d\nLooper: %d", times[0], times[1], times[2]);
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

    private int[][] processImageLooperTimed(int[][] image) {
        long startTime = System.currentTimeMillis();
        int[][] out = processImageLooper(image, THREAD_COUNT);
        long endTime = System.currentTimeMillis();
        times[2] = endTime - startTime;
        return out;

    }

    public int[][] processImageLooper(int[][] image, int threads) {
        for (int j = 0; j < image.length / 2; j++) {
            for (int i = 0; i < image[0].length; i++) {
                image[j][i] = 0;
            }
        }
        return image;
    }

    public static int[][] cloneInt2dArray(int[][] image) {
        int[][] cloneCopy = new int[image.length][];
        for (int i = 0; i < image.length; i++) {
            cloneCopy[i] = new int[image[i].length];
            for (int j = 0; j < image[i].length; j++) {
                cloneCopy[i][j] = image[i][j];
            }
        }
        return cloneCopy;
    }

    public static Bitmap bitmapFromArray(int[][] pixels2d) {
        int width = pixels2d.length;
        int height = pixels2d[0].length;
        int[] pixels = new int[width * height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i + width * j] = pixels2d[i][j];
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
                result[i][j] = pixels[i + width * j];
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("a from b: " + (endTime - startTime));
        return result;
    }


}
