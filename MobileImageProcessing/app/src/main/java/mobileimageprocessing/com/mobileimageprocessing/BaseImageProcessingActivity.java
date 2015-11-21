package mobileimageprocessing.com.mobileimageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class BaseImageProcessingActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "mobileimageprocessing.com.mobileimageprocessing.imageResult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_base_image_processing);
        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra(EXTRA_MESSAGE);
        int[][] seqRes = processImageSequential(arrayFromBitmap(bitmap));
        Bitmap bitmapOutput = bitmapFromArray(seqRes);

        Intent output = new Intent();
        output.putExtra(this.EXTRA_MESSAGE, bitmapOutput);
        setResult(RESULT_OK, output);
        finish();

    }
    private int[][] processImageSequential(int[][] image){
        for(int[] col : image){
            for(int row:col){
                col[row] *=0.5;
            }
        }
        return image;
    }
    public static Bitmap bitmapFromArray(int[][] pixels2d){
        int width = pixels2d.length;
        int height = pixels2d[0].length;
        int[] pixels = new int[width * height];
        int pixelsIndex = 0;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                pixels[pixelsIndex] = pixels2d[i][j];
                pixelsIndex ++;
            }
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
    }

    public static int[][] arrayFromBitmap(Bitmap source){
        int width = source.getWidth();
        int height = source.getHeight();
        int[][] result = new int[width][height];
        int[] pixels = new int[width*height];
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        int pixelsIndex = 0;
        for (int i = 0; i < width; i++)
        {
            for (int j = 0; j < height; j++)
            {
                result[i][j] =  pixels[pixelsIndex];
                pixelsIndex++;
            }
        }
        return result;
    }

}
