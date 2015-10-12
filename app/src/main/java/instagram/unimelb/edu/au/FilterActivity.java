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
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    // Bitmap attributes for holding the original image and filtered images.
    Bitmap bitmap;
    Bitmap origBitmap;
    Bitmap fromFilter;

    private static final String TAG = "FilterActivity";

    // Attributes for assisting with brightness /contrast sliders.
    private int brightnessProgress;
    private int contrastProgress;
    private boolean brightcontrast;

    private ImageView imgView;

    private String filterName;

    //Preview buttons
    ImageButton btnOriginal;
    ImageButton btnFilterInvert;
    ImageButton btnFilter2;
    ImageButton btnFilter3;

    public FilterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FILTER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button

        String imagePath = getIntent().getExtras().getString("photo");

        //Validates if the photo comes from the gallery
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

        // Defining seek bar listener for brightness and contrast control.
        // Only interested in changing the image when the seek bar progress is changed.

        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightnessProgress = progress * 10;
                selectFilterMatrix("brightness",1);
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
                selectFilterMatrix("contrast",1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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

        // Define buttons.
        btnOriginal = (ImageButton) findViewById(R.id.filt_button_original);
        btnFilterInvert = (ImageButton) findViewById(R.id.filt_button_invert);
        btnFilter2 = (ImageButton) findViewById(R.id.filt_button_2);
        btnFilter3 = (ImageButton) findViewById(R.id.filt_button_3);

        btnOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalImage(v);
            }
        });

        // On click listener for all filter buttons call the appropriate methods for each filter.
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


        //To set filtered images on the preview's thumbnails
        InitializeFilterPreviews();


    }

    /**
     * Set filtered images respectively to each button
     */
    private void InitializeFilterPreviews() {
        btnOriginal.setImageBitmap(bitmap);
        selectFilterMatrix("invert", 0);
        selectFilterMatrix("test2", 0);
        selectFilterMatrix("test3", 0);
        bitmap=origBitmap;
    }


    /*
    Save images filtered to share on Instagram
     */
    private void SaveFilteredImage(Bitmap image, String sendTo){
        File pictureFile;
        String type = "image/*";
        pictureFile = CapturePreview.getOutputMediaFile(CapturePreview.MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Log.d(TAG,"Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 0, fos);
            fos.close();
            //Toast.makeText(FilterActivity.this, "Photo Saved", Toast.LENGTH_SHORT).show();

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
            if (sendTo.equals("instagram"))
                createInstagramIntent(type,pictureFile.getPath());
            else
                cropImage(Uri.fromFile(pictureFile));

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

    // Method for reverting displayed image back to original image from camera.
    public void originalImage(View view) {
        imgView.setImageBitmap(origBitmap);
        bitmap=origBitmap;
    }

    // Method for calling filter from the filter_activity view buttons.
    public void selectFilterMatrix(View view) {
        selectFilterMatrix(filterName, 1);
    }



    /**
     *  This method accepts a string filter name which is determined by the button pressed.
     *  Switch statement selects the associated matrix to construct a ColorMatrix which is
     *  to be used to filter the Bitmap object. ColorMatrix object is passed to applyFilter
     *  method to implement filtering.
     * @param filterName the name of the filter we want to apply
     * @param state set 0 for the initial load of thumbnail preview and 1 if not
     */
    public void selectFilterMatrix(String filterName, int state) {

        switch(filterName) {
            case("invert"): {
                float[] filterMatrixArray = {
                        -1, 0, 0, 0, 255,
                        0, -1, 0, 0, 255,
                        0, 0, -1, 0, 255,
                        0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                // bitmap is assigned a new copy of the original image.
                bitmap = origBitmap.copy(origBitmap.getConfig(),true);

                brightcontrast = false;
                if(state==1)
                    applyFilter(filterMatrix,"");
                else
                    applyFilter(filterMatrix,filterName);

                break;
            }

            case("test2"): {
                float[] filterMatrixArray = {
                        1.438f, -0.062f, -0.062f, 0, 0,
                        -0.122f, 1.378f, -0.122f, 0, 0,
                        -0.016f, -0.016f, 1.483f, 0, 0,
                        0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                // bitmap is assigned a new copy of the original image.
                bitmap = origBitmap.copy(origBitmap.getConfig(),true);

                // Indicates the filter applied was not realted to the brightness/contrast seekbar.
                brightcontrast = false;
                if(state==1)
                    applyFilter(filterMatrix,"");
                else
                    applyFilter(filterMatrix,filterName);

                break;
            }

            case("test3"): {
                float[] filterMatrixArray = {
                        0, 1, 0, 0, 0,
                        0, 0, 1, 0, 0,
                        1, 0, 0, 0, 0,
                        0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                // bitmap is assigned a new copy of the original image.
                bitmap = origBitmap.copy(origBitmap.getConfig(),true);

                // Indicates the filter applied was not realted to the brightness/contrast seekbar.
                brightcontrast = false;
                if(state==1)
                    applyFilter(filterMatrix,"");
                else
                    applyFilter(filterMatrix,filterName);

                break;
            }

            case("brightness"): {
                float[] filterMatrixArray = {
                                1, 0, 0, 0, brightnessProgress-125,
                                0, 1, 0, 0, brightnessProgress-125,
                                0, 0, 1, 0, brightnessProgress-125,
                                0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                // Indicates the filter applied was either brightness/contrast seekbar.
                brightcontrast = true;

                applyFilter(filterMatrix,"");
                break;
            }

            case("contrast"): {
                float contrastLevel = contrastProgress/26.0f+0.5f;
                float contrastTranslate = (-0.5f*contrastLevel + 0.5f) * 255f;

                float[] filterMatrixArray = {
                        contrastLevel, 0, 0, 0, contrastTranslate,
                        0, contrastLevel, 0, 0, contrastTranslate,
                        0, 0, contrastLevel, 0, contrastTranslate,
                        0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                // Indicates the filter applied was either brightness/contrast seekbar.
                brightcontrast = true;

                applyFilter(filterMatrix,"");
                break;
            }
        }

    }

    // This method accepts ColorMatrix selected in selectFilterMatrix and draws the filtered
    // pixels to a new Canvas.
    public void applyFilter(ColorMatrix filterMatrix, String filterName) {

        // fromFilter is used to hold a new Bitmap which is generated every time this method
        // is called.
        fromFilter = Bitmap.createBitmap(origBitmap.getWidth(),
                origBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(fromFilter);

        // Paint is generated and applied to each pixel in the object 'bitmap'.
        // For static filters, (not brightness/contrast) bitmap contains a copy of the original
        // image from the camera.
        // For the brightness/contrast filters bitmap contains the last static filter applied.
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(
                filterMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        // If brightness/contrast filter is applied then the output of the filter is displayed
        // in the View.
        if (brightcontrast) {
            imgView.setImageBitmap(fromFilter);
        }
        // If static filter is applied then 'bitmap' is assigned a copy of the filter output
        // allowing brightness / contrast filters to be applied to the filtered image.
        else {
            bitmap = fromFilter.copy(fromFilter.getConfig(), true);

            if (!filterName.equals(""))
                setPreviewThumbnails(filterName,bitmap);
            else
                imgView.setImageBitmap(bitmap);
        }
    }


    private void setPreviewThumbnails(String filterName, Bitmap bitmap) {
        switch(filterName) {
            case ("invert"): {
                btnFilterInvert.setImageBitmap(bitmap);
                break;
            } case ("test2"): {
                btnFilter2.setImageBitmap(bitmap);
                break;
            } case ("test3"): {
                btnFilter3.setImageBitmap(bitmap);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_filter, menu);

        MenuItem item_next = menu.findItem(R.id.action_next);
        item_next.setIcon(R.drawable.next_arrow);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == android.R.id.home) { //Behaviour when back button is pressed
            onBackPressed(); // just go back
        }else if (id == R.id.action_next){
            //Save the image and call to the crop intent
            SaveFilteredImage(bitmap,"crop");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Call of crop intent and return of cropped image onActivityResult
     * @param imageUri
     */
    private void cropImage(Uri imageUri) {
        //call to the crop intent
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setDataAndType(imageUri, "image/*");
        cropIntent.putExtra("crop", "true");

        //desired crop aspect
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);

        //indicate output X and Y
        cropIntent.putExtra("outputX", 256);
        cropIntent.putExtra("outputY", 256);

        //the data will be retrieved on return
        cropIntent.putExtra("return-data", true);

        //The return of the cropped image is handled onActivityResult
        startActivityForResult(cropIntent, 2);
    }


    /**
     * Handles the return from the crop intent and send image to Instagram
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            Bundle extras = data.getExtras();
            Bitmap croppedBitmap = extras.getParcelable("data");

            //The image is sent to Instagram through the share button
            SaveFilteredImage(croppedBitmap, "instagram");
            Toast.makeText(FilterActivity.this, "Select Instagram App to upload your photo", Toast.LENGTH_LONG).show();
        }catch (Exception e) {

        }



    }



}



