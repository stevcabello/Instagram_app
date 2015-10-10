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
import instagram.unimelb.edu.au.models.Profile;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DiscoverFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private GridView gridView;
    private DiscoverAdapter gridAdapter;
    public DiscoverFragment discoverFragment;

    Boolean discoverScrolled = false;
    boDiscover objdiscover;
    Globals globals;
    private View rootView = null;
    private ImageButton ibtn_peopletofollow;
    private boolean initialLoad =false;
    //ImageView discoverPic;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DiscoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String param1, String param2) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
                SuggestedFriendsFragment sff= SuggestedFriendsFragment.newInstance(mParam1,mParam2);
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
        //objdiscover.getDiscoverMedia(discoverFragment, mParam1, mParam2, gridAdapter);


        return rootView;


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !initialLoad) {
            objdiscover.getDiscoverMedia(discoverFragment, mParam1, mParam2, gridAdapter);
            objdiscover.requestMediaIDLikes(mParam1,discoverFragment);
            initialLoad = true;
        }else{
            Log.i("DiscoverFragment", "not visible");

        }
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
       /* try {
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

    public void addDiscoverData(Profile userprofile) {
        //discoverPic = (ImageView)rootView.findViewById(R.id.iv_peopletofollow);

        //discoverPic.setImageBitmap(userprofile.getProfilepic());
    }

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
                    //Toast.makeText(getActivity(), "reach bottom", Toast.LENGTH_SHORT).show();
                    discoverScrolled = false;
                    objdiscover.getDiscoverMedia(discoverFragment, mParam1, mParam2, gridAdapter);
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });





    }

}
