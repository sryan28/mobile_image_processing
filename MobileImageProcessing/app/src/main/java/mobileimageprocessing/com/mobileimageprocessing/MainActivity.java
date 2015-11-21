package mobileimageprocessing.com.mobileimageprocessing;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    Button imageGallery;
    ImageView imageDisplay;
    public static final String EXTRA_MESSAGE = "mobileimageprocessing.com.mobileimageprocessing.imageResult";
    public static Bitmap bitmap;
    public static Bitmap input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = ((Toolbar) findViewById(R.id.toolbar));
        setSupportActionBar(toolbar);
        imageGallery = ((Button) findViewById(R.id.btnGallery));
        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
//                Util.DogDye = false;
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(
                        Intent.createChooser(intent, "Complete action using"), 1);

            }
        });
        imageDisplay = ((ImageView) findViewById(R.id.imageView));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         if (resultCode != RESULT_OK) return;

        switch (requestCode) {

            case 1:
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    //String selectedImagePath = getPath(selectedImageUri);
                    InputStream iStream = null;
                    try {
                        iStream = getContentResolver().openInputStream(selectedImageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
//                    System.out.println(selectedImagePath);
//                    String path = "content://"+selectedImageUri.getAuthority()+""+selectedImageUri.getPath();
//                    System.out.println(path);
                    Bitmap btemp = BitmapFactory.decodeStream(iStream);

                    int[][] seqRes = rotateImageOneEighty(arrayFromBitmap(btemp), btemp.getHeight(), btemp.getWidth());
                    Bitmap bitmapOutput = bitmapFromArray(seqRes);

                    imageDisplay.setImageBitmap(bitmapOutput);
                    System.out.println("works");
                    /// use btemp Image file
                    RadioGroup rGroup = (RadioGroup)findViewById(R.id.radioGroup);
//                    Intent processIntent = new Intent(this.getApplicationContext(), BaseImageProcessingActivity.class);
//                    //processIntent.putExtra(this.EXTRA_MESSAGE, btemp);
//                    bitmap = bitmapOutput;
//                    startActivityForResult(processIntent,2);

//                    int[][] seqRes = rotateImageClockwise(arrayFromBitmap(btemp), btemp.getHeight(), btemp.getWidth());
//                    Bitmap bitmapOutput = bitmapFromArray(seqRes);
//
//                    imageDisplay.setImageBitmap(bitmapOutput);
                    /// use btemp Image file

// This will get the radiobutton in the radiogroup that is checked
//                    RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(rGroup.getCheckedRadioButtonId());
//                    switch(checkedRadioButton.getId()){
//                        case R.id.radioButton:
//                            break;
//                        case R.id.radioButton2:
//                            break;
//                        case R.id.radioButton3:
//                            break;
//                        default:
//                            return;
//                    }
                }
                break;
//            case 2:
//                if(data!=null){
////                    Bitmap btemp = data.getParcelableExtra(this.EXTRA_MESSAGE);
////                    Bitmap btemp = BaseImageProcessingActivity.bOutput;
//                    imageDisplay.setImageBitmap(input);
//
//
//                }
        }

    }
//
//    public int[][] rotateImageClockwise(int[][] image, int height, int width){
//
//        int[][] newImage = new int[width][height];
//
//        for (int i = 0; i < width; i++) {
//            for (int j = 0; j < height; j++) {
//                newImage[i][j] = image[i][j];
//                System.out.println(newImage[i][j]);
//            }
//            System.out.println("\n");
//
//        }
//
//        return newImage;
//    }

    public int[][] rotateImageOneEighty(int[][] image, int height, int width){
        int newHeight = width;
        int newWidth = height;
        System.out.println(width); //3264
        System.out.println(height); //2448

        int[][] newImage = new int[newWidth][newHeight];

        for (int i = 0; i < newWidth; i++) {
            for (int j = 0; j < newHeight; j++) {
//                System.out.print("(" + i + "," + j + ")");
//                newImage[i][j] = image[width-1-j][i];
                newImage[i][j] = image[j][i];
            }
//            System.out.println("");
        }

        return newImage;
    }

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
}
