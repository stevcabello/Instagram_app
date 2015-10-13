package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.SearchAdapter;
import instagram.unimelb.edu.au.businessobject.boSearch;
import instagram.unimelb.edu.au.models.Search;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Search fragment.
 */
public class SearchFragment extends Fragment {

    private static final String ARG_ACCESSTOKEN = "param1";
    private static final String ARG_CLIENTID = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String TAG = "SearchFragment";

    private ListView listView;

    private static SearchAdapter adapter;

    public static SearchFragment searchFragment;

    private View rootView;

    static boSearch objsearch;

    Boolean userScrolled = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESSTOKEN, param1);
        args.putString(ARG_CLIENTID, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
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

        TabLayout tabLayout = (TabLayout)Globals.mainActivity.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        setHasOptionsMenu(true); // to enable the action search bar
        Globals.mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //To avoid reloading the view everytime user access to profile
        if (rootView != null) {
            return rootView;
        }

        Globals.mainActivity.setVisibleFragment(this);  //To let the main activity know that we are in SearchFragment


        // Inflate the layout for this
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchFragment = this;
        adapter = new SearchAdapter(this.getActivity(),R.layout.item_search,new ArrayList<Search>());
        listView = (ListView) rootView.findViewById(R.id.lv_search);
        listView.setAdapter(adapter);

        objsearch = new boSearch();
        return rootView;
    }


    /**
     * Profile results from search 
     * @param searchResult
     */
    public void addProfileMedia(ArrayList<Search> searchResult) {
        adapter.addAll(searchResult);
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

                userScrolled = false;

                myLastVisiblePos = currentFirstVisPos;
            }
        });
    }
    public void search(String querySearch){
        adapter.clear();
        adapter.notifyDataSetChanged();
        if (querySearch!="") {
            objsearch = new boSearch();
            objsearch.getSearch(searchFragment, mParam1, querySearch, adapter);
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
