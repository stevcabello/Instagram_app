package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.LikeAdapter;
import instagram.unimelb.edu.au.models.Likes;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Fragment of the Likes
 */
public class LikesFragment extends Fragment {

    private static ArrayList<Likes> userfeedLikes;

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private LikeAdapter adapter;
    private ListView listView;
    private String TAG = "LikesFragment";


    /**
     * Create a new instance for the LikesFragment
     * @param likes array with previous likes
     * @return LikesFragment's instance
     */
    public static LikesFragment newInstance(ArrayList<Likes> likes) {
        LikesFragment fragment = new LikesFragment();
        userfeedLikes = likes;
        return fragment;
    }

    public LikesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView != null) {
            return rootView;
        }

        Globals.mainActivity.setVisibleFragment(this);

        //Hide the Tabs
        TabLayout tabLayout = (TabLayout) Globals.mainActivity.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        setHasOptionsMenu(true); //to enable the settings action button

        //Reset the previous toolbar settings
        Globals.mainActivity.getSupportActionBar().setTitle(null);
        Globals.mainActivity.getSupportActionBar().setLogo(null);

        //To show the back button and change the title to LIKES
        Globals.mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Globals.mainActivity.getSupportActionBar().setTitle("LIKES");


        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_likes,container,false);

        //Add the likes
        adapter = new LikeAdapter(getActivity(),R.layout.item_like,userfeedLikes);
        // Attach the adapter to a ListView
        listView = (ListView) rootView.findViewById(R.id.lv_likes);
        listView.setAdapter(adapter);


        return rootView;
    }

    // Defaults methods when creating new Fragment
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
