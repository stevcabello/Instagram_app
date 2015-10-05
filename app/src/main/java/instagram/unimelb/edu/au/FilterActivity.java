package instagram.unimelb.edu.au;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;


import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Created by etimire on 7/09/2015.
 */
public class FilterActivity extends AppCompatActivity {

    // Placeholder for camera Bitmap/BitmapDrawable file.

    Bitmap bitmap;
    Bitmap origBitmap;
    Bitmap from_filter;

    private int brightnessProgress;
    private int contrastProgress;
    private float contrastProgressF;
    private float contrastTranslate;

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

        bitmap = BitmapFactory.decodeFile(imagePath,bmOptions);

        bitmap = RotateBitmap(bitmap,90);
        origBitmap = bitmap;

        imgView.setImageBitmap(bitmap);


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

                applyFilter(filterMatrix);
                break;
            }

            case("contrast"): {
                //contrastProgressF = contrastProgress*contrastProgress/13.0f/13.0f;
                contrastProgressF = contrastProgress/13.0f;
                contrastTranslate = (-0.5f*contrastProgressF + 0.5f) * 255f;

                float[] filterMatrixArray = {
                        contrastProgress, 0, 0, 0, contrastTranslate,
                        0, contrastProgress, 0, 0, contrastTranslate,
                        0, 0, contrastProgress, 0, contrastTranslate,
                        0, 0, 0, 1, 0 };

                ColorMatrix filterMatrix = new ColorMatrix(filterMatrixArray);

                applyFilter(filterMatrix);
                break;
            }
        }

    }

    public void applyFilter(ColorMatrix filterMatrix) {

        bitmap = Bitmap.createBitmap(origBitmap.getWidth(),
                origBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(
                filterMatrix));
        canvas.drawBitmap(origBitmap, 0, 0, paint);

        imgView.setImageBitmap(bitmap);
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

