package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.photo.CapturePreview;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoFromCameraFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoFromCameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class PhotoFromCameraFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView;

    //private String TAG = "PhotoFromCameraFragment";
   // private Preview mPreview;
   // Camera mCamera;
    int mNumberOfCameras;
    int mCurrentCamera;  // Camera ID currently chosen
    int mCameraCurrentlyLocked;  // Camera ID that's actually acquired

    // The first rear facing camera
    int mDefaultCameraId;


    private Button btn_switchcamera;

    private Bitmap from_filter;
    private BitmapDrawable drawable_filter;
    private Camera mCamera;
    private CapturePreview cPreview;
    private static final String TAG = "PhotoFromCameraFragment";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFromCameraFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFromCameraFragment newInstance(String param1, String param2) {
        PhotoFromCameraFragment fragment = new PhotoFromCameraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoFromCameraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate called.");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_photo_from_camera,container,false);

        Log.i(TAG, "onCreateView called.");
        // Create a container that will hold a SurfaceView for camera previews
        cPreview = new CapturePreview(getActivity());

//        // Find the total number of cameras available
//        mNumberOfCameras = Camera.getNumberOfCameras();
//
//        // Find the ID of the rear-facing ("default") camera
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        for (int i = 0; i < mNumberOfCameras; i++) {
//            Camera.getCameraInfo(i, cameraInfo);
//            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                mCurrentCamera = mDefaultCameraId = i;
//            }
//        }
//        setHasOptionsMenu(mNumberOfCameras > 1);

        //  cPreview = new CapturePreview(getActivity());

        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.SurfaceView);
        preview.addView(cPreview);

        RelativeLayout switchcamera = (RelativeLayout) rootView.findViewById(R.id.switch_control);
        switchcamera.bringToFront();

        RelativeLayout flashoptions = (RelativeLayout) rootView.findViewById(R.id.flash_op);
        flashoptions.bringToFront();

        RelativeLayout takepic = (RelativeLayout) rootView.findViewById(R.id.take_pic_button);
        takepic.bringToFront();


//        btn_switchcamera = (Button)rootView.findViewById(R.id.btn_switchcamera);
//        btn_switchcamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Release this camera -> mCameraCurrentlyLocked
//                if (mCamera != null) {
//                    mCamera.stopPreview();
//                   cPreview.setCamera(null);
//                    mCamera.release();
//                    mCamera = null;
//                }
//
//                // Acquire the next camera and request Preview to reconfigure
//                // parameters.
//                mCurrentCamera = (mCameraCurrentlyLocked + 1) % mNumberOfCameras;
//                mCamera = Camera.open(mCurrentCamera);
//                mCameraCurrentlyLocked = mCurrentCamera;
//                mCamera.setDisplayOrientation(90);
//                cPreview.switchCamera(mCamera);
//
//                // Start the preview
//                mCamera.startPreview();
//            }
//        });




        // Add a listener to the Capture button
        Button captureButton = (Button) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        Log.d(TAG, "Click to take Picture!!");
                        cPreview.takePicture();
                    }
                }
        );

        Button flashButton = (Button) rootView.findViewById(R.id.btn_flash);
        flashButton.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        Log.d(TAG, "Click to toggle flash.");
                        cPreview.FlashToggle();
                    }
                }
        );

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called.");
        if (!cPreview.active) {
            cPreview = new CapturePreview(getActivity());
            FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.SurfaceView);
            preview.addView(cPreview);
        }

        RelativeLayout switchcamera = (RelativeLayout) rootView.findViewById(R.id.switch_control);
        switchcamera.bringToFront();

        RelativeLayout flashoptions = (RelativeLayout) rootView.findViewById(R.id.flash_op);
        flashoptions.bringToFront();

        RelativeLayout takepic = (RelativeLayout) rootView.findViewById(R.id.take_pic_button);
        takepic.bringToFront();
//
//        // Use mCurrentCamera to select the camera desired to safely restore
//        // the fragment after the camera has been changed
//        mCamera = Camera.open(mCurrentCamera);
//        mCamera.setDisplayOrientation(90);
//        mCameraCurrentlyLocked = mCurrentCamera;
//        cPreview.setCamera(mCamera);
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//
//        // Because the Camera object is a shared resource, it's very
//        // important to release it when the activity is paused.
//        if (mCamera != null) {
//            cPreview.setCamera(null);
//            mCamera.release();
//            mCamera = null;
//        }
//    }



    @Override
    public void onPause() {
        super.onPause();
        cPreview.CameraActivityPause();          // release the camera immediately on pause event
        Log.i(TAG, "onPause called.");
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "onAttach called.");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i(TAG, "onDetach called.");
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}









//TODO: not delete because this is useful to Preview class which doesnt have the camera release issue
//public class PhotoFromCameraFragment extends Fragment {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;
//    private View rootView;
//
//    private String TAG = "PhotoFromCameraFragment";
//    private Preview mPreview;
//    Camera mCamera;
//    int mNumberOfCameras;
//    int mCurrentCamera;  // Camera ID currently chosen
//    int mCameraCurrentlyLocked;  // Camera ID that's actually acquired
//
//    // The first rear facing camera
//    int mDefaultCameraId;
//
//
//    private Button btn_switchcamera;
//
////    private Bitmap from_filter;
////    private BitmapDrawable drawable_filter;
////    private Camera mCamera;
////    private CapturePreview cPreview;
////    private static final String TAG = "PhotoFromCameraFragment";
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment PhotoFromCameraFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static PhotoFromCameraFragment newInstance(String param1, String param2) {
//        PhotoFromCameraFragment fragment = new PhotoFromCameraFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    public PhotoFromCameraFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//
//        // Inflate the layout for this fragment
//        rootView = inflater.inflate(R.layout.fragment_photo_from_camera,container,false);
//
//        // Create a container that will hold a SurfaceView for camera previews
//        mPreview = new Preview(getActivity());
//
//        // Find the total number of cameras available
//        mNumberOfCameras = Camera.getNumberOfCameras();
//
//        // Find the ID of the rear-facing ("default") camera
//        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//        for (int i = 0; i < mNumberOfCameras; i++) {
//            Camera.getCameraInfo(i, cameraInfo);
//            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//                mCurrentCamera = mDefaultCameraId = i;
//            }
//        }
//        setHasOptionsMenu(mNumberOfCameras > 1);
//
//      //  cPreview = new CapturePreview(getActivity());
//
//        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.SurfaceView);
//        preview.addView(mPreview);
//
//        RelativeLayout switchcamera = (RelativeLayout) rootView.findViewById(R.id.switch_control);
//        switchcamera.bringToFront();
//
//        RelativeLayout flashoptions = (RelativeLayout) rootView.findViewById(R.id.flash_op);
//        flashoptions.bringToFront();
//
//
//        btn_switchcamera = (Button)rootView.findViewById(R.id.btn_switchcamera);
//        btn_switchcamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Release this camera -> mCameraCurrentlyLocked
//                if (mCamera != null) {
//                    mCamera.stopPreview();
//                    mPreview.setCamera(null);
//                    mCamera.release();
//                    mCamera = null;
//                }
//
//                // Acquire the next camera and request Preview to reconfigure
//                // parameters.
//                mCurrentCamera = (mCameraCurrentlyLocked + 1) % mNumberOfCameras;
//                mCamera = Camera.open(mCurrentCamera);
//                mCameraCurrentlyLocked = mCurrentCamera;
//                mCamera.setDisplayOrientation(90);
//                mPreview.switchCamera(mCamera);
//
//                // Start the preview
//                mCamera.startPreview();
//            }
//        });
//
//
//
//
//        // Add a listener to the Capture button
//        Button captureButton = (Button) rootView.findViewById(R.id.capture_button);
//        captureButton.setOnClickListener(
//
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // get an image from the camera
//                        Log.d(TAG, "Click to take Picture!!");
//                        mPreview.takePicture();
//                    }
//                }
//        );
//
//
//
//        return rootView;
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        // Use mCurrentCamera to select the camera desired to safely restore
//        // the fragment after the camera has been changed
//        mCamera = Camera.open(mCurrentCamera);
//        mCamera.setDisplayOrientation(90);
//        mCameraCurrentlyLocked = mCurrentCamera;
//        mPreview.setCamera(mCamera);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        // Because the Camera object is a shared resource, it's very
//        // important to release it when the activity is paused.
//        if (mCamera != null) {
//            mPreview.setCamera(null);
//            mCamera.release();
//            mCamera = null;
//        }
//    }
//
//
//
////    @Override
////    public void onPause() {
////        super.onPause();
////        cPreview.CameraActivityPause();          // release the camera immediately on pause event
////    }
//
//
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }
//
//}
