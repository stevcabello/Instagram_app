package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.SuggestedFriendsAdapter;
import instagram.unimelb.edu.au.businessobject.boDiscover;
import instagram.unimelb.edu.au.models.SuggestedFriends;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Class to charge all the information obtained
 * after applying some rules on the business
 * class.
 */
public class SuggestedFriendsFragment extends Fragment {
    private static final String ARG_ACCESSTOKEN = "param1";
    private static final String ARG_CLIENTID = "param2";

    private String mAccesstoken;
    private String mClientID;

    private OnFragmentInteractionListener mListener;
    private View rootView;

    private ListView listView;
    private SuggestedFriendsAdapter gridAdapter;

    boDiscover objDiscover;
    public SuggestedFriendsFragment suggestedFriendsFragment;
    Boolean userScrolled = false;

    /**
     * Create new instance of a fragment according with the
     * parameters
     * @param accesstoken Access token for the API Instagram
     * @param clientid ID of the user of the application
     * @return
     */
    public static SuggestedFriendsFragment newInstance(String accesstoken, String clientid) {
        SuggestedFriendsFragment fragment = new SuggestedFriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESSTOKEN, accesstoken);
        args.putString(ARG_CLIENTID, clientid);
        fragment.setArguments(args);
        return fragment;
    }

    public SuggestedFriendsFragment() {
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
        //To avoid reloading the view everytime user access to profile..Pay attention
        if (rootView != null) {
            return rootView;
        }

        TabLayout tabLayout = (TabLayout)Globals.mainActivity.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        Globals.mainActivity.setVisibleFragment(this);
        setHasOptionsMenu(true); //to enable the settings action button

        Globals.mainActivity.getSupportActionBar().setTitle("PEOPLE SUGGESTIONS");
        Globals.mainActivity.getSupportActionBar().setLogo(null);
        Globals.mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Inflate the layout for this
        rootView = inflater.inflate(R.layout.fragment_suggested_friends,container,false);

        suggestedFriendsFragment = this;

        listView = (ListView)rootView.findViewById(R.id.lv_suggestedFriends);
        gridAdapter = new SuggestedFriendsAdapter(this.getActivity(),R.layout.item_suggested_friends,new ArrayList<SuggestedFriends>());
        listView.setAdapter(gridAdapter);

        objDiscover = new boDiscover();
        objDiscover.getSuggestedFriendsMedia(suggestedFriendsFragment, mAccesstoken, mClientID, gridAdapter,0);
        // Inflate the layout for this fragment
        return rootView;
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
     * Charge the list of the suggested friends
     * on the fragment that contains this list.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    public void addSuggestedFriends(SuggestedFriends suggestedFriends) {

        Log.i("addSuggestedFriends", "Fragment");
        gridAdapter.addAll(suggestedFriends);
    }
}
