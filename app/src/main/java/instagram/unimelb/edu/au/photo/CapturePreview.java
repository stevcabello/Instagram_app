package instagram.unimelb.edu.au.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import instagram.unimelb.edu.au.FilterActivity;

/**
 * Currently not using this Class due to problem on SurfaceDestroyed
 */
public class CapturePreview extends SurfaceView implements SurfaceHolder.Callback{

    public File pictureFile;
    public static Bitmap mBitmap;
    SurfaceHolder holder;   // Holders are meant for controlling a surface size and format.
    static Camera mCamera;    // Camera class deprecated in much later API 21.
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = "CapturePreview";
    public boolean active = false;
    public boolean flashOn;
    Camera.Size mPreviewSize;
    List<Camera.Size> mSupportedPreviewSizes;
    boolean mSurfaceCreated = false;
    static String myApp = "InstagramApp"; //A folder with this name will be created and all the photos taken will be stored here
    int width;
    int height;
    int vmid1;
    int vmid2;
    int hmid1;
    int hmid2;
    Paint paint;


    public CapturePreview(Context context) {
        super(context);

        holder = getHolder();         // Allows access to the SurfaceHolder for this SurfaceView.
        holder.addCallback(this);     // Adds a Callback interface (itself) to the holder object.
        // holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  Apparantly this is automatic..
        this.setWillNotDraw(false);

        Log.i(TAG, "CapturePreview object constructed.");
        active = true;
    }

    // This method is from SurfaceHolder.Callback interface.
    // Overriding the surfaceChanged method. Called whenever change to format or size of surface.
    // Just when a rotation occurs?
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.getSupportedPreviewSizes();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

    // Definitely will happen when the CameraPreview is instantiated.
    // This method is from SurfaceHolder.Callback interface.
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            mCamera = Camera.open(); // attempt to get a Camera instance
            mCamera.setPreviewDisplay(holder);
            mCamera.setDisplayOrientation(90);
            updateFlash(flashOn);
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "Failed to open Camera");
            e.printStackTrace();
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
        Log.i(TAG, "Surface created. Camera opened.");
    }

    // This method is from SurfaceHolder.Callback interface.
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //trying to fix the camera crash
        this.getHolder().removeCallback(this);
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            Log.i(TAG, "Surface destroyed. Camera released.");
            mCamera = null;
        }
        active = false;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        width = canvas.getWidth();
        height = canvas.getHeight();
        vmid1 = width/3;
        vmid2 = 2*width/3;
        hmid1 = height/3;
        hmid2 = 2*height/3;


        canvas.drawLine(vmid1, 0, vmid1, height, paint);
        canvas.drawLine(vmid2, 0, vmid2, height, paint);
        canvas.drawLine(0, hmid1, width, hmid1, paint);
        canvas.drawLine(0, hmid2, width, hmid2, paint);
    }

    /***
     *
     *  Take a picture and and convert it from bytes[] to Bitmap.
     *
     */
    public void takePicture(){
        // This is creating a new PictureCallback to pass to the takePicture method.

        Log.i(TAG, "Flash status: " + mCamera.getParameters().getFlashMode());
        Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i(TAG, "PictureCallbackcreated 2ND");
                pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null){
                    Log.d(TAG, "Error creating media file, check storage permissions: " );
                    return;
                }

                // What is actually writing a file here?? Is it the getOutputMediaFile or is it
                // the FileOutputStream write method?

                try {
                    Log.i(TAG, "Writing to file");
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Log.i(TAG, "File closed.");
                } catch (FileNotFoundException e) {
                    Log.d(TAG, "File not found: " + e.getMessage());
                } catch (IOException e) {
                    Log.d(TAG, "Error accessing file: " + e.getMessage());
                }


                //Create the Filter as a Fragment : not working well yet

//                FragmentTransaction fragmentTransaction = Globals.mainActivity.getSupportFragmentManager().beginTransaction();
//                FilterFragment ff= FilterFragment.newInstance(pictureFile.getAbsolutePath());
//                fragmentTransaction.replace(R.id.fly_photo_fragment, ff);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();

                //Call to filter activity
                Intent intent = new Intent(getContext(),FilterActivity.class);
                Log.i("photo", pictureFile.getAbsolutePath());
                intent.putExtra("photo",pictureFile.getAbsolutePath());
                getContext().startActivity(intent);

            }

        };

        mCamera.takePicture(null, null, mPictureCallback);




        Log.d(TAG, "takePicture launched...");



        /////// Open saved image in new activity ///////


    }

    public void setFlashOn(Boolean flashState) {
        flashOn = flashState;
    }

    // This is called whenever toggle changes.
    public void FlashToggle() {
        Log.i(TAG, "Before flash toggle launched: "+ flashOn);
        flashOn = !flashOn;

        Log.i(TAG, "After flash toggle launched: "+ flashOn);

        updateFlash(flashOn);
    }

    public void updateFlash(Boolean flash) {

        Camera.Parameters param = mCamera.getParameters();

        if (flashOn) {
            param.setFlashMode("on");
            mCamera.setParameters(param);
            Log.i(TAG, "Get flash mode (expect on): " + mCamera.getParameters().getFlashMode());}
        else {
            param.setFlashMode("off");
            mCamera.setParameters(param);
            Log.i(TAG, "Get flash mode (expect off): " + mCamera.getParameters().getFlashMode()); }
    }

    public void CameraActivityPause() {
        //surfaceDestroyed(holder);
        Log.i(TAG, "CameraActivityPause");
    }




    /** Create a File for saving an image */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        Log.i(TAG, "Attempt to create file with getOutputMediaFile");

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), myApp);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
            Log.i(TAG, "Filename:"+"IMG_"+ timeStamp + ".jpg");
            Log.i(TAG, "Path:"+mediaStorageDir.getPath());
        } else {
            return null;
        }
        return mediaFile;
    }


}