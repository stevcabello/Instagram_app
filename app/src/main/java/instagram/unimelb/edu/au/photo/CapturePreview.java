package instagram.unimelb.edu.au.photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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


    public CapturePreview(Context context) {
        super(context);

        holder = getHolder();         // Allows access to the SurfaceHolder for this SurfaceViiew.
        holder.addCallback(this);     // Adds a Callback interface (itself) to the holder object.
        // holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  Apparantly this is automatic..
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
        }
        catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e("getCameraInstance", "Failed to open Camera");
            e.printStackTrace();
        }
    }

    // This method is from SurfaceHolder.Callback interface.
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.getHolder().removeCallback(this);
        mCamera.stopPreview();
        mCamera.release();
    }


    /***
     *
     *  Take a picture and and convert it from bytes[] to Bitmap.
     *
     */
    public void takePicture(){
        // This is creating a new PictureCallback to pass to the takePicture method.


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



    public void CameraActivityPause() {
        surfaceDestroyed(holder);
    }

    public void CameraActivityResume() {surfaceCreated(holder);}



    /** Create a File for saving an image */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        Log.i(TAG, "Attempt to create file with getOutputMediaFile");

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "InstagramCamera_API14");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("InstagramCamera_API14", "failed to create directory");
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