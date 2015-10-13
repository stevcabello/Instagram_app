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
import instagram.unimelb.edu.au.adapters.FollowingActivityFeedAdapter;
import instagram.unimelb.edu.au.businessobject.boFollowing;
import instagram.unimelb.edu.au.models.FollowingActivityFeed;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Following Activity FeedFragment
 */
public class FollowingActivityFeedFragment extends Fragment {
    private static final String ARG_ACCESSTOKEN = "param1";
    private static final String ARG_CLIENTID = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView=null;

    private ListView listView;
    private FollowingActivityFeedAdapter gridAdapter;

    boFollowing objFollowers;
    public FollowingActivityFeedFragment followingActivityFragment;
    Boolean userScrolled = false;

    /**
     * New Fragment Instance
     * @param accessToken
     * @param clientId
     * @return
     */
    public static FollowingActivityFeedFragment newInstance(String accessToken, String clientId) {
        FollowingActivityFeedFragment fragment = new FollowingActivityFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESSTOKEN, accessToken);
        args.putString(ARG_CLIENTID, clientId);
        fragment.setArguments(args);
        return fragment;
    }

    public FollowingActivityFeedFragment() {
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

        setHasOptionsMenu(true); //to enable the settings action button

        // Inflate the layout for this
        rootView = inflater.inflate(R.layout.fragment_following_activity_feed, container, false);

        followingActivityFragment = this;

        listView = (ListView) rootView.findViewById(R.id.lv_followeractivityfeed);
        gridAdapter = new FollowingActivityFeedAdapter(this.getActivity(), R.layout.item_followingactivityfeed, new ArrayList<FollowingActivityFeed>());
        listView.setAdapter(gridAdapter);

        objFollowers = new boFollowing();
        objFollowers.getProfileMedia(followingActivityFragment, mParam1, mParam2, gridAdapter);


        return rootView;
    }

    /**
     * Add follower profile media to the adapter
     * @param follower
     */

    public void addProfileMedia(FollowingActivityFeed follower) {

        gridAdapter.addAll(follower);

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
                if (firstVisibleItem + visibleItemCount >= totalItemCount && userScrolled
                        && currentFirstVisPos > myLastVisiblePos && Globals.FOLLOWERACTIVITY_MEDIA_MAX_ID != "-1") {

                    userScrolled = false;
                    objFollowers.getProfileMedia(followingActivityFragment, mParam1, mParam2, gridAdapter);
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });
    }


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






}
