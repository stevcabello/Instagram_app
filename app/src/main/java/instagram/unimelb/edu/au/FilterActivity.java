package instagram.unimelb.edu.au;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import instagram.unimelb.edu.au.photo.GLToolbox;
import instagram.unimelb.edu.au.photo.TextureRenderer;

/**
 * Created by etimire on 7/09/2015.
 */
public class FilterActivity extends AppCompatActivity implements GLSurfaceView.Renderer {


    // Placeholder for camera Bitmap/BitmapDrawable file.

    Bitmap from_filter;
    BitmapDrawable drawable_filter;

    // From the effect site. Hello effect.

    private GLSurfaceView mEffectView;
    private int[] mTextures = new int[2];
    private EffectContext mEffectContext;
    private Effect mEffect;
    private TextureRenderer mTexRenderer = new TextureRenderer();
    private int mImageWidth;
    private int mImageHeight;
    private boolean mInitialized = false;
    int mCurrentEffect;
    Bitmap bitmap;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FILTER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // back button

        mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);
        mEffectView.setEGLContextClientVersion(2);
        mEffectView.setRenderer(this);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCurrentEffect = R.id.none;


       String imagePath = getIntent().getExtras().getString("photo");


        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 2; //reduce size of image


        //bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        bitmap = BitmapFactory.decodeFile(imagePath,bmOptions);

        bitmap = RotateBitmap(bitmap,90);
        //bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);



    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }



//        // This replaces the Image defined in the XML file.
    //mainImageView = (ImageView) findViewById(R.id.imageView);
    //mainImageView.setImageDrawable(drawable_camera);

    // This sets up the GLSurfaceView for second filter implementation.
   // mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);






    public void applyFilter0(View view) {
//        mainImageView.setImageDrawable(drawable_camera);
//
        setCurrentEffect(R.id.none);
        mEffectView.requestRender();
    }
//
//    public void applyFilter1(View view) {
//
//        from_filter = BitmapFactory.decodeResource(getResources(), R.drawable.smile2);
//        drawable_filter = new BitmapDrawable(from_filter);
//        mainImageView.setImageDrawable(drawable_filter);
//
//    }


    public void applyFilter(View view) {
        setCurrentEffect(1);
        mEffectView.requestRender();
    }

    // Other methods related to effect.

    public void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }

    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0);

        // Load input bitmap
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
          //      R.drawable.smile2);


        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);

        // Upload to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Set texture parameters
        GLToolbox.initTexParams();
    }

    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
        }

        mEffect = effectFactory.createEffect(EffectFactory.EFFECT_LOMOISH);
    }

    private void applyEffect() {
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
    }

    private void renderResult() {
        if (mCurrentEffect != R.id.none) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1]);
        }
        else {
            //saveFrame=true;
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0]);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        }
        if (mCurrentEffect != R.id.none) {
            //if an effect is chosen initialize it and apply it to the texture
            initEffect();
            applyEffect();
        }
        renderResult();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
            mTexRenderer.updateViewSize(width, height);
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
