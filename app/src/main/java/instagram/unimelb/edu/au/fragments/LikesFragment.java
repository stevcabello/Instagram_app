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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LikesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LikesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikesFragment extends Fragment {

    private static ArrayList<Likes> userfeedLikes;

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private LikeAdapter adapter;
    private ListView listView;
    private String TAG = "LikesFragment";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment LikesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LikesFragment newInstance(ArrayList<Likes> param1) {
        LikesFragment fragment = new LikesFragment();
        userfeedLikes = param1;

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

        //To avoid reloading the view everytime user access to profile
        if (rootView != null) {
            return rootView;
        }

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
