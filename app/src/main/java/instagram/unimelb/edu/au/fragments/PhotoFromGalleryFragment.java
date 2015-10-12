package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.database.Cursor;
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

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.PhotoFromGalleryAdapter;
import instagram.unimelb.edu.au.utils.Globals;

/**
 *
 */
public class PhotoFromGalleryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
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
    public static ArrayList<ArrayList<String>> gallery;

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
        setHasOptionsMenu(true); //To enable the next button
        galleryFragment = this;


        gridView = (GridView)rootView.findViewById(R.id.gv_gallery);
        gridAdapter = new PhotoFromGalleryAdapter(this.getActivity(),R.layout.item_photo_gallery,new ArrayList<ArrayList<String>>());
        gridView.setAdapter(gridAdapter);
        imagePreview = (ImageView)rootView.findViewById(R.id.iv_preview);
        gallery = new ArrayList<ArrayList<String>>();
        callShowGallery();
        loadImage(gallery.get(0).get(0), imagePreview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.d("PhotoGalleryFragment", "Position " + Integer.toString(position));
                loadImage(gallery.get(position).get(0), imagePreview);
            }
        });

        return rootView;
    }


    /**
     * Method that loads regular image for preview instead of thumbnail
     * @param imagePath
     * @param image
     */
    public void loadImage(String imagePath, ImageView image){
        Globals.GALLERY_SELECTEDPATH = imagePath;
        Picasso.with(getContext())
                .load("file://" + imagePath)
                        .into(image);
    }

    /**
     * Method that calls show Gallery
     */
    public void callShowGallery(){
        ArrayList<ArrayList<String>> usermedia = getImagesPath();
        ShowGallery(usermedia);
    }

    /**
     * Recieve a list of images and send them to the adapter, handles the scrolling
     * @param usermedia
     */
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
                Log.i("Photogallery", Long.toString(Globals.GALLERY_MEDIA_MAX_ID));
                //To only send a new request when user has scrolled down until reach the bottom and while the totalitemcount is lesser than the number of posts
                if (firstVisibleItem + visibleItemCount >= totalItemCount && userScrolled && currentFirstVisPos > myLastVisiblePos && Globals.GALLERY_MEDIA_MAX_ID != 0) {
                    callShowGallery();
                    userScrolled = false;
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });
    }

    /**
     * Method that get an arrayList  of the paths with  the images in the gallery
     * @return
     */
    public ArrayList<ArrayList<String>> getImagesPath() {
        Uri uri;
        ArrayList<ArrayList<String>> listOfAllImages = new ArrayList<ArrayList<String>>();
        Cursor cursor;
        int column_index_data, column_index_id;
        String pathOfImage = null;
        long imageId = 0L ;
        //uri = android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        uri =MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        /*String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN};*/

        /*Columns used on the cursor*/
        String[] projection = new String[]{
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };
        /*To show order by date and paginates by 15*/
        String orderBy = MediaStore.Images.ImageColumns._ID + " DESC LIMIT 15";
        /*To filter images*/
        String whereimageId = MediaStore.Images.Media._ID + " < ?";
        String valuesIs[] = {Long.toString(Globals.GALLERY_MEDIA_MAX_ID)};
        try {
            cursor = this.getActivity().getContentResolver().query(uri, projection, whereimageId,
                    valuesIs, orderBy);

            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            // column_index_id = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.IMAGE_ID);
            column_index_id = cursor.getColumnIndexOrThrow( MediaStore.Images.Media._ID);

            //Cursor that fills the list with the thumbnail path and regular path
            if (cursor != null){
                while (cursor.moveToNext()) {
                    pathOfImage = cursor.getString(column_index_data);
                    //imageId = cursor.getString(column_index_id);
                    imageId = cursor.getLong(column_index_id);
                    Log.d("PhotoFromGallery", Long.toString(imageId));
                    ArrayList<String> iteration =new ArrayList<String>();
                    Cursor thumbcursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(
                            this.getActivity().getContentResolver(),imageId,
                            MediaStore.Images.Thumbnails.MINI_KIND,
                            null );
                    String thumbnail ="";
                    if( thumbcursor != null && thumbcursor.getCount() > 0 ) {
                        thumbcursor.moveToFirst();
                        thumbnail = thumbcursor.getString( thumbcursor.getColumnIndex( MediaStore.Images.Thumbnails.DATA ) );
                        thumbcursor.close();
                    }

                    iteration.add(pathOfImage);
                    if (!thumbnail.equals("")) {
                        iteration.add(thumbnail);
                    }
                    else {
                        iteration.add(pathOfImage);
                    }
                    listOfAllImages.add(iteration);
                    gallery.add(iteration);
                }
                Globals.GALLERY_MEDIA_MAX_ID=imageId;
                cursor.close();
            }
        } catch (Exception e){
            Log.e("PhotoFromGallery", e.toString());
        }

        return listOfAllImages;

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
