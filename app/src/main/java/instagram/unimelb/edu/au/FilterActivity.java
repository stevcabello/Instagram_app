package instagram.unimelb.edu.au;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import instagram.unimelb.edu.au.photo.CapturePreview;

/**
 * Created by etimire on 7/09/2015.
 */
public class FilterActivity extends AppCompatActivity {

    // Placeholder for camera Bitmap/BitmapDrawable file.

    Bitmap bitmap;
    Bitmap origBitmap;
    Bitmap fromFilter;

    private static final String TAG = "FilterActivity";

    private int brightnessProgress;
    private int contrastProgress;
    private float contrastLevel;
    private float contrastTranslate;
    private boolean brightcontrast;

    private Canvas canvas;

    private Toolbar toolbar;

    private ImageView imgView;

    private String filterName;

    public FilterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FILTER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button

        String imagePath = getIntent().getExtras().getString("photo");
        /*
        Validates if the photo comes from the gallery
         */
        boolean galleryOrigin = getIntent().getBooleanExtra("gallery", false);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 4; //reduce size of image

        imgView = (ImageView) findViewById(R.id.filter_view);

        // Define seekbars.
        SeekBar brightness = (SeekBar) findViewById(R.id.brightness_slider);
        brightness.setMax(26);
        brightness.setProgress(13);
        SeekBar contrast = (SeekBar) findViewById(R.id.contrast_slider);
        contrast.setMax(26);
        contrast.setProgress(13);

        // Define buttons.
        Button btnOriginal = (Button) findViewById(R.id.filt_button_original);
        Button btnFilterInvert = (Button) findViewById(R.id.filt_button_invert);
        Button btnFilter2 = (Button) findViewById(R.id.filt_button_2);
        Button btnFilter3 = (Button) findViewById(R.id.filt_button_3);
        ImageButton btnNext = (ImageButton) findViewById(R.id.ib_next);

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightnessProgress = progress*10;
                selectFilterMatrix("brightness");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        contrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                contrastProgress = progress;
                selectFilterMatrix("contrast");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        btnOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalImage(v);
            }
        });

        btnFilterInvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterName = "invert";
                selectFilterMatrix(v);
            }
        });

        btnFilter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterName = "test2";
                selectFilterMatrix(v);
            }
        });

        btnFilter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterName = "test3";
                selectFilterMatrix(v);
            }
        });
        /*
         Onclick, save and shares photo filtered
         */
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveFilteredImage(bitmap);
            }
        });
        bitmap = BitmapFactory.decodeFile(imagePath,bmOptions);
        /*
        If the photo comes from the gallery it does not rotate it
         */
        if (galleryOrigin==false){
        bitmap = RotateBitmap(bitmap,90);
        }
        origBitmap = bitmap;

        imgView.setImageBitmap(bitmap);


    }
    /*
    Save images filtered to share on Instagram
     */
    private void SaveFilteredImage(Bitmap image){
        File pictureFile;
        String type = "image/*";
       /* Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);*/
        pictureFile = CapturePreview.getOutputMediaFile(CapturePreview.MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Log.d(TAG,"Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast.makeText(FilterActivity.this, "Photo Saved", Toast.LENGTH_SHORT).show();

            /*
            Adding information to the gallery
             */
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, pictureFile.getName());
            values.put(MediaStore.Images.Media.DESCRIPTION,pictureFile.getName());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, pictureFile.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, pictureFile.getName().toLowerCase(Locale.US));
            values.put("_data", pictureFile.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            /*
             Calling the share intent to Instagram app
            */
            createInstagramIntent(type,pictureFile.getPath());
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

    }
    /*
    Instagram intent
     */
    private void createInstagramIntent(String type, String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    public void originalImage(View view) {

        imgView.setImageBitmap(origBitmap);
    }

    public void selectFilterMatrix(View view) {
        selectFilterMatrix(filterName);
    }

    public void selectFilterMatrix(String filterName) {

        switch(filterName) {
            case("invert"): {
                float[] filterMatrixArray = {
                        -1, 0, 0, 0, 255,
                        0, -1, 0, 0, 255,
                        0, 0, -1, 0, 255,
                        0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                bitmap = origBitmap.copy(origBitmap.getConfig(),true);

                brightcontrast = false;

                applyFilter(filterMatrix);

                break;
            }

            case("test2"): {
                float[] filterMatrixArray = {
                        1.438f, -0.062f, -0.062f, 0, 0,
                        -0.122f, 1.378f, -0.122f, 0, 0,
                        -0.016f, -0.016f, 1.483f, 0, 0,
                        0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                bitmap = origBitmap.copy(origBitmap.getConfig(),true);

                brightcontrast = false;

                applyFilter(filterMatrix);

                break;
            }

            case("test3"): {
                float[] filterMatrixArray = {
                        0, 1, 0, 0, 0,
                        0, 0, 1, 0, 0,
                        1, 0, 0, 0, 0,
                        0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                bitmap = origBitmap.copy(origBitmap.getConfig(),true);

                brightcontrast = false;

                applyFilter(filterMatrix);

                break;
            }

            case("brightness"): {
                float[] filterMatrixArray = {
                                1, 0, 0, 0, brightnessProgress-125,
                                0, 1, 0, 0, brightnessProgress-125,
                                0, 0, 1, 0, brightnessProgress-125,
                                0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                brightcontrast = true;

                applyFilter(filterMatrix);
                break;
            }

            case("contrast"): {
                contrastLevel = contrastProgress/26.0f+0.5f;
                contrastTranslate = (-0.5f*contrastLevel + 0.5f) * 255f;

                float[] filterMatrixArray = {
                        contrastLevel, 0, 0, 0, contrastTranslate,
                        0, contrastLevel, 0, 0, contrastTranslate,
                        0, 0, contrastLevel, 0, contrastTranslate,
                        0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                brightcontrast = true;

                applyFilter(filterMatrix);
                break;
            }
        }

    }

    public void applyFilter(ColorMatrix filterMatrix) {

        fromFilter = Bitmap.createBitmap(origBitmap.getWidth(),
                origBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(fromFilter);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(
                filterMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        if (brightcontrast) {
            imgView.setImageBitmap(fromFilter);
        }
        else {
            bitmap = fromFilter.copy(fromFilter.getConfig(), true);
            imgView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == android.R.id.home) { //Behaviour when back button is pressed
            onBackPressed(); // just go back
        }

        return super.onOptionsItemSelected(item);
    }

}

