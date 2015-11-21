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
                    imageDisplay.setImageBitmap(btemp);
                    /// use btemp Image file
                    RadioGroup rGroup = (RadioGroup)findViewById(R.id.radioGroup);
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
            case 2:
                if(data!=null){
                    Bitmap btemp = data.getParcelableExtra(this.EXTRA_MESSAGE);
                    imageDisplay.setImageBitmap(btemp);

                }
        }

    }
}
