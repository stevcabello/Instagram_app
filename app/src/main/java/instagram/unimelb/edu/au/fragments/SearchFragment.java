package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;

import java.util.ArrayList;

import instagram.unimelb.edu.au.MainActivity;
import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.SearchAdapter;
import instagram.unimelb.edu.au.businessobject.boSearch;
import instagram.unimelb.edu.au.models.Search;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String TAG = "SearchFragment";

    private ListView listView;

    private SearchAdapter adapter;

    public SearchFragment searchFragment;

    private View rootView;
    private String querySearch="";
    boSearch objsearch;

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
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        rootView = inflater.inflate(R.layout.fragment_search, container, false);
        searchFragment = this;
        adapter = new SearchAdapter(this.getActivity(),R.layout.item_search,new ArrayList<Search>());
        listView = (ListView) rootView.findViewById(R.id.lv_search);
        listView.setAdapter(adapter);

        objsearch = new boSearch();
        return rootView;
    }



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
                //To only send a new request when user has scrolled down until reach the bottom and while the totalitemcount is lesser than the number of posts
                /*TODO: Pagination was giving error, not sure why so i commented this part */
                //if (firstVisibleItem + visibleItemCount >= totalItemCount && userScrolled && currentFirstVisPos > myLastVisiblePos) {
                    //Toast.makeText(getActivity(), "reach bottom", Toast.LENGTH_SHORT).show();
                    userScrolled = false;

                    //objsearch.getSearch(searchFragment, mParam1, querySearch, adapter);
                //}
                myLastVisiblePos = currentFirstVisPos;
            }
        });
    }
    public void search(){
        adapter.clear();
        adapter.notifyDataSetChanged();
        if (querySearch!="") {
            objsearch = new boSearch();
            objsearch.getSearch(searchFragment, mParam1, querySearch, adapter);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                querySearch = query;
                search();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*TODO: This option takes a lot of time in the app*/
                /*search(newText);*/
                return  true;
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
        /*try {
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
