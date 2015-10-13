package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.YouActivityFeedAdapter;
import instagram.unimelb.edu.au.businessobject.boActivityFeed;
import instagram.unimelb.edu.au.models.YouActivityFeed;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link YouActivityFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link YouActivityFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class YouActivityFeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ACCESSTOKEN = "param1";
    private static final String ARG_CLIENTID = "param2";
    public YouActivityFeedFragment youActivityFeedFragment;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    boActivityFeed objActivityFeed;
    private OnFragmentInteractionListener mListener;
    private YouActivityFeedAdapter listAdapter;
    private ListView listView;
    private Boolean userScrolled = false;
    private View rootView=null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment YouActivityFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static YouActivityFeedFragment newInstance(String param1, String param2) {
        YouActivityFeedFragment fragment = new YouActivityFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESSTOKEN, param1);
        args.putString(ARG_CLIENTID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public YouActivityFeedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_ACCESSTOKEN);
            mParam2 = getArguments().getString(ARG_CLIENTID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //To avoid reloading the view everytime user access to profile
        if (rootView != null) {
            return rootView;
        }

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_you_activity_feed,container,false);
        youActivityFeedFragment=this;
        // Construct the data source

        // Create the adapter to convert the array to views
        listAdapter = new YouActivityFeedAdapter(getActivity(), R.layout.item_youactivityfeed , new ArrayList<YouActivityFeed>());
        // Attach the adapter to a ListView
        listView = (ListView) rootView.findViewById(R.id.lv_youactivityfeed);
        listView.setAdapter(listAdapter);

        objActivityFeed = new boActivityFeed();
        /*
        TODO: compare with profile fragment, also fix the images attributes, also this method should have an adapter
        */
        objActivityFeed.getProfileMedia(youActivityFeedFragment, mParam1, mParam2,listAdapter);


        return rootView;

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
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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
    public void addYouActivity(ArrayList<YouActivityFeed> youuseractivity) {

        listAdapter.addAll(youuseractivity);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int myLastVisiblePos = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    userScrolled = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int currentFirstVisPos = view.getFirstVisiblePosition();

                //To only send a new request when user has scrolled down until reach the bottom and while the totalitemcount is lesser than the number of posts
                if (firstVisibleItem + visibleItemCount >= totalItemCount && userScrolled && currentFirstVisPos > myLastVisiblePos && Globals.YOUACTIVITY_MEDIA_MAX_ID != "-1" ) {
                    //Toast.makeText(getActivity(), "reach bottom", Toast.LENGTH_SHORT).show();
                    userScrolled = false;
                    objActivityFeed.getProfileMedia(youActivityFeedFragment, mParam1, mParam2,listAdapter);
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });




    }
}
