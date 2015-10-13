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
 * Fragment for the userfeed
 */
public class UserFeedFragment extends Fragment {


    private static final String ARG_ACCESS_TOKEN = "access_token";
    private static final String ARG_CLIENT_ID = "client_id";


    private static String mAccessToken;
    private String mClientId;

    private static UserFeedAdapter adapter;


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

    private static ArrayList<UserFeed> byLocationUserFeed = new ArrayList<>();
    private static ArrayList<UserFeed> currentUserfeeddata = new ArrayList<>();


    /**
     * Creates new instance for UserFeedFragment
     * @param accesstoken access_token for the application
     * @param client_id id of the authenticated user
     * @return a new instance of UserFeedFragment
     */
    public static UserFeedFragment newInstance(String accesstoken, String client_id) {
        UserFeedFragment fragment = new UserFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESS_TOKEN, accesstoken);
        args.putString(ARG_CLIENT_ID, client_id);
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
            mAccessToken = getArguments().getString(ARG_ACCESS_TOKEN);
            mClientId = getArguments().getString(ARG_CLIENT_ID);
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
        adapter = new UserFeedAdapter(getActivity(),R.layout.item_userfeed, mAccessToken,new ArrayList<UserFeed>());

        //A custom listview to provide sticky headers functionality
        listView = (StickyListHeadersListView) rootView.findViewById(R.id.lv_userfeed);
        listView.setDrawingListUnderStickyHeader(true);
        listView.setAreHeadersSticky(true);

        listView.setAdapter(adapter);

        listView.setStickyHeaderTopOffset(-20);

        objuserfeed = new boUserFeed();

        objuserfeed.getUserFeedData(userfeedFragment, mAccessToken, adapter, sortType); // the first sort is by the default type (Datetime)

        //To handle refresh of userfeed when user swipe down onto the screen
        swipeLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { //When refresh we clear some parameters
                swipeLayout.setRefreshing(false);
                userSwiped = true;
                Globals.USERFEED_MAX_ID = "";
                adapter.clear();
                byLocationUserFeed.clear();
                currentUserfeeddata.clear();
                objuserfeed.getUserFeedData(userfeedFragment, mAccessToken, adapter, sortType);

            }
        });


        return rootView;

    }

    /**
     * Is called from MainActivity to initialize the feed and sort the data based on datetime or location
     * @param sortBy 0 by Date/Time , 1 by Location
     */
    public static void getData(int sortBy, double latitude, double longitude){

        userLatitude = latitude;
        userLongitude = longitude;

        if (sortBy != sortType) { // If there is a change in the type of sorting all the list is refreshed

            changeSortType = true;
            adapter.clear();

            byLocationUserFeed.clear();
            currentUserfeeddata.clear();

            //just to avoid any issue with the onscrolllistener method
            userScrolled = false;
            lockScroll = true;

            Globals.USERFEED_MAX_ID = "";
        }
        sortType = sortBy;
        objuserfeed.getUserFeedData(userfeedFragment, mAccessToken, adapter, sortBy);
    }


    /**
     * Add data to the userfeed's listview
     * @param userfeeddata new UserFeed data to be added
     * @param sortBy 0 to identify by date/time sort and 1 to identify by Location sort
     */
    public void addUserFeedData(ArrayList<UserFeed> userfeeddata, final int sortBy) {

        ArrayList<UserFeed> newUserfeeddata;

        lockScroll = false; //release the scroll to sense when user reach bottom of list

        if (userSwiped) adapter.clear();

        if (sortBy == 1) { // If sort feed by Location
            Log.i("uriimages", "e5");
            byLocationUserFeed.addAll(userfeeddata); //we store all the by location userfeed data
            newUserfeeddata = sortByLocation(byLocationUserFeed,userLatitude,userLongitude);

            //To notify to the user in case new closer post have been added on top of the listview while scrolling down on the list
            if (currentUserfeeddata.size() > 0 ) {
                if (closerLocationschanged(currentUserfeeddata, newUserfeeddata))
                    Toast.makeText(getActivity(), "Closer posts have been added on top of the list", Toast.LENGTH_SHORT).show();
            }
            currentUserfeeddata.clear();
            currentUserfeeddata.addAll(newUserfeeddata);

            adapter.clear();
            adapter.addAll(newUserfeeddata);
            adapter.notifyDataSetChanged();
            if (changeSortType) listView.setAdapter(adapter);
            Globals.switchState = true;
        }else { //If the sort if by Date/Time
            adapter.addAll(userfeeddata);
            if (changeSortType) listView.setAdapter(adapter);
            Globals.switchState = false;
        }

        changeSortType = false;

        if (userSwiped) {
            userSwiped = false;

            listView.setAdapter(adapter);

            //To notify when the userfeed is updated
            Toast toast = Toast.makeText(getActivity(), "UserFeed is up to date", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 10);
            toast.show();
        }


        //Onscrolllistener to load new data when user scrolls down.
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
                        && currentFirstVisPos > myLastVisiblePos - 1 && !userSwiped && !lockScroll) {
                    userScrolled = false;
                    lockScroll = true; // lock the scroll to avoid duplicated requests
                    objuserfeed.getUserFeedData(userfeedFragment, mAccessToken, adapter, sortBy);
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });


    }

    /**
     * Looks for changed on top ten post in the userfeed location-based
     * @param currentArr current array of UserFeed data
     * @param newArr new array of UserFeed data
     * @return true in case of change on top ten post, false otherwise.
     */
    public boolean closerLocationschanged(ArrayList<UserFeed> currentArr, ArrayList<UserFeed> newArr){

        int totalCurrent = currentArr.size();
        int totalNew = newArr.size();
        int total = 0;

        //Just to avoid any problem with index out of range
        if (totalCurrent>10 && totalNew>10)
            total = 10;
        else{
            if (totalCurrent > totalNew) total = totalNew;
            else total = totalCurrent;
        }

        for(int i=0; i<total; i++){
            float distanceC = currentArr.get(i).getDistanceToAuthUser();
            float distanceN = newArr.get(i).getDistanceToAuthUser();

            if (distanceC != distanceN) return true;
        }

        return false;
    }



    /**
     * Sorts the userfeed's list by closer locations to authenticated user.
     * @param arrUserFeed
     * @param userLat
     * @param userLon
     * @return the sort by location arraylist of userfeed
     */
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


    // Default methods when creating new Fragment
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
