package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.UserFeedAdapter;
import instagram.unimelb.edu.au.businessobject.boUserFeed;
import instagram.unimelb.edu.au.models.UserFeed;
import instagram.unimelb.edu.au.utils.Globals;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFeedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private UserFeedAdapter adapter;

    //private ListView listView;
    private StickyListHeadersListView listView;
    boUserFeed objuserfeed;

    Boolean userScrolled = false; //user scrolling down throug list
    Boolean userSwiped = false; //user swiping to refresh
    Boolean lockScroll = false; //to avoid duplicated requests due to scroll issues

    private String TAG = "UserFeedFragment";

    public UserFeedFragment userfeedFragment;

    private View rootView;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeLayout;

    //private boolean fadeHeader = true;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFeedFragment newInstance(String param1, String param2) {
        UserFeedFragment fragment = new UserFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserFeedFragment() {
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

        //To avoid reloading the view everytime user access to userfeed
        if (rootView != null) {
            return rootView;
        }

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_userfeed,container,false);


        userfeedFragment = this;


        // Create the adapter to convert the array to views
        adapter = new UserFeedAdapter(getActivity(),R.layout.item_userfeed, mParam1 ,new ArrayList<UserFeed>());

        listView = (StickyListHeadersListView) rootView.findViewById(R.id.lv_userfeed);
        listView.setDrawingListUnderStickyHeader(true);
        listView.setAreHeadersSticky(true);

        listView.setAdapter(adapter);

        listView.setStickyHeaderTopOffset(-20);

        objuserfeed = new boUserFeed();
        Log.i("uriimages", "e1");
        objuserfeed.getUserFeedData(userfeedFragment, mParam1, adapter);

        //To handle refresh of userfeed when user swipe down onto the screen
        swipeLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(false);
                userSwiped = true;
                Globals.USERFEED_MAX_ID = "";
                adapter.clear();
                Log.i("uriimages", "e3");
                objuserfeed.getUserFeedData(userfeedFragment, mParam1, adapter);

            }
        });


        return rootView;

    }


    /**
     * Adds data to the user feed listview
     * @param userfeeddata
     */
    public void addUserFeedData(ArrayList<UserFeed> userfeeddata) {

        Log.i("uriimages", "e4");
        lockScroll = false; //release the scroll to sense when user reach bottom of list

        if (userSwiped) adapter.clear();

        adapter.addAll(userfeeddata);

        if (userSwiped) {
            userSwiped = false;
            //adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);

            Toast toast = Toast.makeText(getActivity(), "UserFeed is up to date", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 0);
            toast.show();
        }



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
                if (firstVisibleItem + visibleItemCount >= totalItemCount && userScrolled && currentFirstVisPos > myLastVisiblePos -1 && !userSwiped && !lockScroll) {
                    userScrolled = false;
                    lockScroll = true; // lock the scroll to avoi duplicated requests
                    Log.i("uriimages", "e2");
                    objuserfeed.getUserFeedData(userfeedFragment, mParam1,adapter);
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });


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
/*        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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
