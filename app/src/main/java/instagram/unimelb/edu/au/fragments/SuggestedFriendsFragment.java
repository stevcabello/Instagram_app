package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.SuggestedFriendsAdapter;
import instagram.unimelb.edu.au.businessobject.boDiscover;
import instagram.unimelb.edu.au.models.SuggestedFriends;
import instagram.unimelb.edu.au.utils.Globals;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SuggestedFriendsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SuggestedFriendsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuggestedFriendsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private View rootView;

    private ListView listView;
    private SuggestedFriendsAdapter gridAdapter;

    boDiscover objDiscover;
    public SuggestedFriendsFragment suggestedFriendsFragment;
    Boolean userScrolled = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SuggestedFriendsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SuggestedFriendsFragment newInstance(String param1, String param2) {
        SuggestedFriendsFragment fragment = new SuggestedFriendsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //To avoid reloading the view everytime user access to profile..Pay attention
        if (rootView != null) {
            return rootView;
        }

        setHasOptionsMenu(true); //to enable the settings action button


        // Inflate the layout for this
        rootView = inflater.inflate(R.layout.fragment_suggested_friends,container,false);

        suggestedFriendsFragment = this;

        listView = (ListView)rootView.findViewById(R.id.lv_suggestedFriends);
        gridAdapter = new SuggestedFriendsAdapter(this.getActivity(),R.layout.item_suggested_friends,new ArrayList<SuggestedFriends>());
        listView.setAdapter(gridAdapter);

        objDiscover = new boDiscover();
        objDiscover.getSuggestedFriendsMedia(suggestedFriendsFragment, mParam1, mParam2, gridAdapter);
        // Inflate the layout for this fragment
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
        //try {
        //    mListener = (OnFragmentInteractionListener) activity;
        //} catch (ClassCastException e) {
        //    throw new ClassCastException(activity.toString()
        //            + " must implement OnFragmentInteractionListener");
        //}
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

    public void addSuggestedFriends(ArrayList<SuggestedFriends> suggestedFriends) {

        Log.i("addSuggestedFriends","Fragment");
        Log.i("Size Suggested Friends",String.valueOf(suggestedFriends.size()));
        gridAdapter.addAll(suggestedFriends);

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
                if (firstVisibleItem + visibleItemCount >= totalItemCount && userScrolled && currentFirstVisPos > myLastVisiblePos && Globals.SUGGESTEDFRIENDS_MEDIA_MAX_ID != "-1") {
                    //Toast.makeText(getActivity(), "reach bottom", Toast.LENGTH_SHORT).show();
                    userScrolled = false;
                    objDiscover.getSuggestedFriendsMedia(suggestedFriendsFragment, mParam1, mParam2,gridAdapter);
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });
    }

}
