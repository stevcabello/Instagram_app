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
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.UserFeedAdapter;
import instagram.unimelb.edu.au.businessobject.boUserFeed;
import instagram.unimelb.edu.au.models.Comments;
import instagram.unimelb.edu.au.models.Likes;
import instagram.unimelb.edu.au.models.UserFeed;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;
import instagram.unimelb.edu.au.utils.Utils;
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
    private static String mParam1;
    private String mParam2;

    private static UserFeedAdapter adapter;

    //private ListView listView;
    private static StickyListHeadersListView listView;
    static boUserFeed objuserfeed;

    static Boolean userScrolled = false; //user scrolling down throug list
    Boolean userSwiped = false; //user swiping to refresh
    static Boolean lockScroll = false; //to avoid duplicated requests due to scroll issues

    private String TAG = "UserFeedFragment";

    public static UserFeedFragment userfeedFragment;

    private View rootView;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeLayout;

    private static Boolean changeSortType = false;

    private static int sortType = 0; //Initialized to just sort by datetime (as we get it from Instagram API)

    private static double userLatitude;
    private static  double userLongitude;

    private ArrayList<UserFeed> byLocationUserFeed = new ArrayList<>();

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

        setHasOptionsMenu(true); //To enable the switch

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
        objuserfeed.getUserFeedData(userfeedFragment, mParam1, adapter, sortType); // the first sort is by the default type (Datetime)

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
                byLocationUserFeed.clear();
                objuserfeed.getUserFeedData(userfeedFragment, mParam1, adapter, sortType);

            }
        });


        return rootView;

    }

    /**
     * Is called from MainActivity to initialize the feed and sort the data based on datetime or location
     * @param sortBy
     */
    public static void getData(int sortBy, double latitude, double longitude){

        userLatitude = latitude;
        userLongitude = longitude;

        if (sortBy != sortType) { // If there is a change in the type of sorting all the list is refreshed
            Log.i("uriimages", "change in sorttype");
            changeSortType = true;
            adapter.clear();

            //just to avoid any bug with the onscrolllistener method
            userScrolled = false;
            lockScroll = true;

            Globals.USERFEED_MAX_ID = "";
        }
        sortType = sortBy;
        objuserfeed.getUserFeedData(userfeedFragment, mParam1, adapter, sortBy);
    }


    /**
     * Adds data to the user feed listview
     * @param userfeeddata
     */
    public void addUserFeedData(ArrayList<UserFeed> userfeeddata, final int sortBy) {

        ArrayList<UserFeed> newUserfeeddata;
        Log.i("uriimages", "e4");
        lockScroll = false; //release the scroll to sense when user reach bottom of list

        if (userSwiped) adapter.clear();

        if (sortBy == 1) { // If sort feed by Location
            Log.i("uriimages", "e5");
            byLocationUserFeed.addAll(userfeeddata); //we store all the by location userfeed data
            newUserfeeddata = sortByLocation(byLocationUserFeed,userLatitude,userLongitude);
            adapter.clear();
            adapter.addAll(newUserfeeddata);
            //listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            if (changeSortType) listView.setAdapter(adapter);
            Globals.switchState = true;
        }else {
            Log.i("uriimages", "e6");
            adapter.addAll(userfeeddata);
            if (changeSortType) listView.setAdapter(adapter);
            Globals.switchState = false;
        }

        changeSortType = false;

        if (userSwiped) {
            userSwiped = false;
            //adapter.notifyDataSetChanged();
            //if (sortBy!=1)
            listView.setAdapter(adapter);

            Toast toast = Toast.makeText(getActivity(), "UserFeed is up to date", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 10);
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
                if (firstVisibleItem + visibleItemCount >= totalItemCount && userScrolled && currentFirstVisPos > myLastVisiblePos - 1 && !userSwiped && !lockScroll) {
                    userScrolled = false;
                    lockScroll = true; // lock the scroll to avoi duplicated requests
                    Log.i("uriimages", "e2");
                    objuserfeed.getUserFeedData(userfeedFragment, mParam1, adapter, sortBy);
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });


    }


    public ArrayList<UserFeed> sortByLocation(ArrayList<UserFeed> arrUserFeed, final double userLat, final double userLon) {

        float distance;

        //Set the distance to authenticated user in all the elements of the arraylist
        for (UserFeed userFeed : arrUserFeed) {
            distance = Utils.DistanceBetween(userLat,userLon,userFeed.getLatitude(),userFeed.getLongitude());
            userFeed.setDistanceToAuthUser(distance);
        }

        //Sort the array based on the distance to the authenticated user
        Collections.sort(arrUserFeed, new Comparator<UserFeed>() {
            @Override
            public int compare(UserFeed lhs, UserFeed rhs) {
                return Float.valueOf(lhs.getDistanceToAuthUser()).compareTo(rhs.getDistanceToAuthUser());
            }
        });

        return arrUserFeed;

    }


    /**
     * Updates the userfeed's listview with the item received via Bluetooth
     *
     * @param userFeedJsonStr
     */
    public static void setBluetoothUserFeedItem(String userFeedJsonStr) {

        Toast.makeText(userfeedFragment.getActivity(),"In Range post has arrived",Toast.LENGTH_SHORT).show();

        try {
            Log.i("Bluetooth", "jsonStr: " + userFeedJsonStr);
            JSONObject jsonObject = new JSONObject(userFeedJsonStr);


            ArrayList<Comments> comments = new ArrayList<Comments>();

            JSONArray jsonArrayComments = jsonObject.getJSONArray("comments");
            for(int i=0; i<jsonArrayComments.length();i++){
                String commentuser = jsonArrayComments.getJSONObject(i).getString("username");
                String commenttext = jsonArrayComments.getJSONObject(i).getString("text");
                String commentcreatedtime = jsonArrayComments.getJSONObject(i).getString("created_time");

                String commentprofilepic_url = jsonArrayComments.getJSONObject(i).getString("profilepic_url");
                ImageView commentprofilepic = new ImageView(userfeedFragment.getActivity());
                ImageRequest.makeImageRequest(commentprofilepic_url, userfeedFragment.getActivity(), commentprofilepic, adapter);

                Comments comment = new Comments(commentuser,commenttext,commentcreatedtime,commentprofilepic,commentprofilepic_url);
                comments.add(comment);

            }

            ArrayList<Likes> likes = new ArrayList<Likes>();

            JSONArray jsonArrayLikes = jsonObject.getJSONArray("likes");
            for(int i=0; i<jsonArrayLikes.length();i++){
                String likeusername = jsonArrayLikes.getJSONObject(i).getString("username");
                String likefullname = jsonArrayLikes.getJSONObject(i).getString("full_name");

                String likeprofilepic_url = jsonArrayLikes.getJSONObject(i).getString("profilepic_url");
                ImageView likeprofilepic = new ImageView(userfeedFragment.getActivity());
                ImageRequest.makeImageRequest(likeprofilepic_url, userfeedFragment.getActivity(), likeprofilepic, adapter);

                Likes like = new Likes(likeusername,likefullname,likeprofilepic,likeprofilepic_url);
                likes.add(like);

            }


            //Create the userfeed's item
            UserFeed userFeed = new UserFeed();

            //Set the photo's post
            String photo_url = jsonObject.getString("photo_url");
            userFeed.setPhoto_url(photo_url);
            ImageView photo = new ImageView(userfeedFragment.getActivity());
            ImageRequest.makeImageRequest(photo_url, userfeedFragment.getActivity(), photo, adapter);
            userFeed.setPhoto(photo);

            //Set the post's profilepic
            String profilepic_url = jsonObject.getString("profilepic_url");
            userFeed.setProfilepic_url(profilepic_url);
            ImageView profilepic = new ImageView(userfeedFragment.getActivity());
            ImageRequest.makeImageRequest(profilepic_url, userfeedFragment.getActivity(), profilepic, adapter);
            userFeed.setProfilepic(profilepic);

            userFeed.setUsername(jsonObject.getString("username"));
            userFeed.setCreated_time(jsonObject.getString("created_time"));
            userFeed.setNumLikes(Integer.parseInt(jsonObject.getString("numlikes")));
            userFeed.setDescription(jsonObject.getString("description"));
            userFeed.setNumcomments(Integer.parseInt(jsonObject.getString("numcomments")));
            userFeed.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
            userFeed.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));
            userFeed.setComments(comments);
            userFeed.setLikes(likes);
            userFeed.setTag("In Range"); //set the "In Range" tag on the post


            ArrayList<UserFeed> userFeeds = new ArrayList<>();
            userFeeds = adapter.getAllData(); //get all the items
            userFeeds.add(0,userFeed); //add this item on top of the listview

            adapter.clear();
            adapter.addAll(userFeeds); //add the new ArrayList (with the new item on top of the list)
            adapter.notifyDataSetChanged();




        }catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(userfeedFragment.getActivity(),"In range post couldn't be delivered",Toast.LENGTH_SHORT).show();
            Log.i("Bluetooth",e.getMessage());
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
