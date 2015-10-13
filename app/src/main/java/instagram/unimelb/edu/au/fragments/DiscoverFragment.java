package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageButton;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.DiscoverAdapter;
import instagram.unimelb.edu.au.businessobject.boDiscover;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Class to charge all the information obtained
 * after applying some rules on the business
 * class Discover.
 */
public class DiscoverFragment extends Fragment {
    private static final String ARG_ACCESSTOKEN = "param1";
    private static final String ARG_CLIENTID = "param2";

    private String mAccesstoken;
    private String mClientID;

    private OnFragmentInteractionListener mListener;


    private GridView gridView;
    private DiscoverAdapter gridAdapter;
    public DiscoverFragment discoverFragment;

    Boolean discoverScrolled = false;
    boDiscover objdiscover;
    private View rootView = null;
    private ImageButton ibtn_peopletofollow;
    private boolean initialLoad =false;

    /**
     * Create new instance of a fragment according with the
     * parameters
     * @param accesstoken Access token for the API Instagram
     * @param clientid ID of the user of the application
     * @return
     */
    public static DiscoverFragment newInstance(String accesstoken, String clientid) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESSTOKEN, accesstoken);
        args.putString(ARG_CLIENTID, clientid);
        fragment.setArguments(args);
        return fragment;
    }

    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAccesstoken = getArguments().getString(ARG_ACCESSTOKEN);
            mClientID = getArguments().getString(ARG_CLIENTID);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         //To avoid reloading the view everytime user access to discover
        if (rootView != null) {
            return rootView;
        }

        setHasOptionsMenu(true); //to enable the search button view

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_discover,container,false);


        discoverFragment = this;

        ibtn_peopletofollow = (ImageButton)rootView.findViewById(R.id.ibtn_peopletofollow);
        ibtn_peopletofollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open the suggested friends fragment
                FragmentTransaction fragmentTransaction = Globals.mainActivity.getSupportFragmentManager().beginTransaction();
                SuggestedFriendsFragment sff= SuggestedFriendsFragment.newInstance(mAccesstoken,mClientID);
                fragmentTransaction.replace(R.id.fly_discover_fragment, sff);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                Globals.mainActivity.setVisibleFragment(sff);
            }
        });

        gridView = (GridView) rootView.findViewById(R.id.gv_discover);
        gridAdapter = new DiscoverAdapter(this.getActivity(), R.layout.item_discover, new ArrayList<ImageItem>());
        gridView.setAdapter(gridAdapter);

        objdiscover = new boDiscover();

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !initialLoad) {
            objdiscover.getDiscoverMedia(discoverFragment, mAccesstoken, mClientID, gridAdapter);
            objdiscover.requestMediaIDLikes(mAccesstoken,discoverFragment);
            initialLoad = true;
        }else{
            Log.i("DiscoverFragment", "not visible");

        }
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

    /**
     * Charge the information of the most popular
     * Instagram users.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    /**
     * Add the images of the most popular Instagram users.
     * @param discovermedia List of images of popular users.
     */
    public void addDiscoverMedia(ArrayList<ImageItem> discovermedia) {

        gridAdapter.addAll(discovermedia);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int myLastVisiblePos = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    discoverScrolled = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int currentFirstVisPos = view.getFirstVisiblePosition();

                //To only send a new request when user has scrolled down until reach the bottom and while the totalitemcount is lesser than the number of posts
                if (firstVisibleItem + visibleItemCount >= totalItemCount && discoverScrolled && currentFirstVisPos > myLastVisiblePos) {
                    discoverScrolled = false;
                    objdiscover.getDiscoverMedia(discoverFragment, mAccesstoken, mClientID, gridAdapter);
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });
    }

}
