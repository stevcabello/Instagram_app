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
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

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
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;
    private View rootView;

    private boolean flashOn = false;

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
    }

    // When fragment first created the CapturePreview object is assigned to the SurfaceView and
    // the camera buttons are put on top of the SurfaceView and also have functionality associated
    // with them.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_photo_from_camera,container,false);

        // Create a container that will hold a SurfaceView for camera previews
        cPreview = new CapturePreview(getActivity());

        // Add CapturePreview object to the FrameLayout.
        FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.SurfaceView);
        preview.addView(cPreview);

        // Associates setFlashOn method to the flash button in the layout.
        RelativeLayout flashoptions = (RelativeLayout) rootView.findViewById(R.id.flash_op);
        flashoptions.bringToFront();
        cPreview.setFlashOn(flashOn);

        // Associates button press to invoking takePicture method in CapturePreview class.
        RelativeLayout takepic = (RelativeLayout) rootView.findViewById(R.id.take_pic_button);
        takepic.bringToFront();
        Button captureButton = (Button) rootView.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cPreview.takePicture();
                    }
                }
        );

        // Listener for the flash togglebutton calls FlashToggle method in CapturePreview class.
        ToggleButton flashButton = (ToggleButton) rootView.findViewById(R.id.btn_flash);
        flashButton.setOnCheckedChangeListener(

                        new CompoundButton.OnCheckedChangeListener() {

                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                // get an image from the camera
                                flashOn = isChecked;
                                cPreview.FlashToggle();
                            }
                        }
        );
        return rootView;
    }


    // Whenever fragment is resumed check if current CapturePreview object is still active.
    // If not create new CapturePreview object and arrange buttons as per onCreateView.
    @Override
    public void onResume() {
        super.onResume();
        if (!cPreview.active) {
            cPreview = new CapturePreview(getActivity());
            FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.SurfaceView);
            preview.addView(cPreview);
        }

        RelativeLayout flashoptions = (RelativeLayout) rootView.findViewById(R.id.flash_op);
        flashoptions.bringToFront();
        cPreview.setFlashOn(flashOn);

        RelativeLayout takepic = (RelativeLayout) rootView.findViewById(R.id.take_pic_button);
        takepic.bringToFront();
    }

    // onPause
    @Override
    public void onPause() {
        super.onPause();
        cPreview.photoFragmentPause();          // release the camera immediately on pause event
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}