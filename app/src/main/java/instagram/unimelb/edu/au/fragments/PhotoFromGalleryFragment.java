package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.PhotoFromGalleryAdapter;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoFromGalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoFromGalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoFromGalleryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView;
    private PhotoFromGalleryAdapter gridAdapter;
    private GridView gridView;
    private ImageView imagePreview;
    public PhotoFromGalleryFragment galleryFragment;
    Boolean userScrolled = false;
    ArrayList<ArrayList<String>> gallery;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotoFromGalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoFromGalleryFragment newInstance(String param1, String param2) {
        PhotoFromGalleryFragment fragment = new PhotoFromGalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoFromGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //To avoid reloading the view everytime user access to discover
        if (rootView != null) {
            return rootView;
        }
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_photo_from_gallery,container,false);
        galleryFragment = this;
        gridView = (GridView)rootView.findViewById(R.id.gv_gallery);
        gridAdapter = new PhotoFromGalleryAdapter(this.getActivity(),R.layout.item_photo_gallery,new ArrayList<ArrayList<String>>());
        gridView.setAdapter(gridAdapter);
        imagePreview = (ImageView)rootView.findViewById(R.id.iv_preview);
        gallery=getImagesPath();
        loadImage(gallery.get(0).get(0), imagePreview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.d("PhotoGalleryFragment", "Position "+Integer.toString(position));
                loadImage(gallery.get(position).get(0), imagePreview);
            }
        });
        callShowGallery();
        return rootView;
    }
    //load regular image for preview instead of thumbnail
    public void loadImage(String imageId, ImageView image){
        String regularImage = getImagePath(imageId);
        Globals.GALLERY_SELECTEDPATH = regularImage;
        Picasso.with(getContext())
                .load("file://" + regularImage)
                        .into(image);
    }
    public void callShowGallery(){
        ArrayList<ArrayList<String>> usermedia= paginationGallery(gallery);
        ShowGallery(usermedia);
    }

    public void ShowGallery(ArrayList<ArrayList<String>> usermedia) {

        gridAdapter.addAll(usermedia);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int myLastVisiblePos = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    userScrolled = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int currentFirstVisPos = view.getFirstVisiblePosition();
                Log.i("Photogallery", Integer.toString(Globals.GALLERY_MEDIA_MAX_ID));
                //To only send a new request when user has scrolled down until reach the bottom and while the totalitemcount is lesser than the number of posts
                if (firstVisibleItem + visibleItemCount >= totalItemCount && userScrolled && currentFirstVisPos > myLastVisiblePos &&  Globals.GALLERY_MEDIA_MAX_ID != -1 ) {
                    callShowGallery();
                    userScrolled = false;
                    Log.i("Photogallery", "In");

                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });
    }

    public ArrayList<ArrayList<String>> getImagesPath() {
        Uri uri;
        ArrayList<ArrayList<String>> listOfAllImages = new ArrayList<ArrayList<String>>();
        Cursor cursor;
        int column_index_data, column_index_id;
        String pathOfImage = null;
        String imageId = null;
        uri = android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Thumbnails.IMAGE_ID };

        cursor = this.getActivity().getContentResolver().query(uri, projection, null,
                null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_id = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID);
        while (cursor.moveToNext()) {
            pathOfImage = cursor.getString(column_index_data);
            imageId = cursor.getString(column_index_id);
            ArrayList<String> iteration =new ArrayList<String>();
            iteration.add(imageId);
            iteration.add(pathOfImage);
            listOfAllImages.add(iteration);
        }
        return listOfAllImages;
    }

    public ArrayList<ArrayList<String>> paginationGallery(ArrayList<ArrayList<String>> imageItems){
        ArrayList<ArrayList<String>> usermedia = new ArrayList<>();
        int sizeGallery = imageItems.size()                ;
        int toShow = Globals.GALLERY_MEDIA_MAX_ID+15;
        if (sizeGallery < toShow){
            toShow = sizeGallery;
        }
        for(int j = Globals.GALLERY_MEDIA_MAX_ID; j < toShow; j++){
            usermedia.add(imageItems.get(j));
            Globals.GALLERY_MEDIA_MAX_ID = Globals.GALLERY_MEDIA_MAX_ID + 1;
        }
        if  (Globals.GALLERY_MEDIA_MAX_ID>= sizeGallery){
            Globals.GALLERY_MEDIA_MAX_ID=-1;
        }
        return usermedia;
    }
    public String getImagePath( String reterievedImageId) {
        String[] columnsReturn = {MediaStore.Images.Media.DATA};
        String whereimageId = MediaStore.Images.Media._ID + " LIKE ?";
        String valuesIs[] = {"%" + reterievedImageId};
        String imagePath ="";
        Cursor mCursor = this.getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columnsReturn, whereimageId, valuesIs, null);
        int rawDataPath = mCursor.getColumnIndex(MediaStore.Images.Media.DATA);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            imagePath = mCursor.getString(rawDataPath);
        }
        return imagePath;
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
