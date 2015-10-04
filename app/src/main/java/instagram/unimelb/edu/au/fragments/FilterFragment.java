package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.utils.Globals;

public class FilterFragment extends Fragment {

    private static final String IMG_PATH = "image_path";

    // TODO: Rename and change types of parameters
    private String imagePath;


    private OnFragmentInteractionListener mListener;

    private View rootView;
    private ImageView imgView;

    // Placeholder for camera Bitmap/BitmapDrawable file.

    Bitmap from_filter;

    Bitmap bitmap;
    private Button btnFilter;
    private Button btnOriginal;

    private int mImageWidth;
    private int mImageHeight;

    private ColorMatrix colorMatrix;

    public static FilterFragment newInstance(String param1) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putString(IMG_PATH, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imagePath = getArguments().getString(IMG_PATH);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_filter,container,false);

        Globals.mainActivity.getSupportActionBar().setTitle("FILTER");
        Globals.mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgView = (ImageView) rootView.findViewById(R.id.filterview);
        btnOriginal = (Button)rootView.findViewById(R.id.filt_button_original);
        btnFilter = (Button)rootView.findViewById(R.id.filt_button_invert);

        btnOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter0(v);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilter(v);
            }
        });



        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 2; //reduce size of image

        bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);

        imgView.setImageBitmap(bitmap);

        return rootView;
    }

    public void applyFilter0(View view) {}

    public void applyFilter(View view) {}


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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }




}
