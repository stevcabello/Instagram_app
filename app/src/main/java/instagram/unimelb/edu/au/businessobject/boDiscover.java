package instagram.unimelb.edu.au.businessobject;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import instagram.unimelb.edu.au.adapters.DiscoverAdapter;
import instagram.unimelb.edu.au.adapters.SuggestedFriendsAdapter;
import instagram.unimelb.edu.au.fragments.DiscoverFragment;
import instagram.unimelb.edu.au.fragments.SuggestedFriendsFragment;
import instagram.unimelb.edu.au.models.FollowingActivityFeed;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.models.SuggestedFriends;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;
import instagram.unimelb.edu.au.utils.Utils;

/**
 * Class that established the rules to control the DiscoverFragment
 * Discover option in th Instagram application.
 */
public class boDiscover {
    private String TAG = boDiscover.class
            .getSimpleName();
    private String tag_json_obj = "jobj_req";
    ProgressDialog pDialog;
    private ArrayList<SuggestedFriends> friendsFriends = new ArrayList<>();
    private ArrayList<SuggestedFriends> friendsFriendsFinal = new ArrayList<>();
    //Min number of likes of a suggested friend on the list
    private static int COMMON_LIKES_THRESHOLD = 5;

    /**
     * Method that charge information about the most popular Instagram users
     * @param discoverFragment Fragment to charge the most popular users images
     * @param accesstoken Access code for the Instagram API
     * @param clientid User ID of the application
     * @param adapter Class name that manage the Fragment Class
     */
    public void getDiscoverMedia(final DiscoverFragment discoverFragment, String accesstoken, String clientid, final DiscoverAdapter adapter) {

        pDialog = new ProgressDialog(discoverFragment.getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        final ArrayList<ImageItem> discoverMedia = new ArrayList<>();

        // API Link used on the request
        // https://api.instagram.com/v1/media/popular
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/media/popular"
                + "/?access_token=" + accesstoken, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());

                        try {
                            JSONArray array = response.getJSONArray("data");

                            for(int i=0; i<array.length(); i++)
                            {
                                String uriImage = array.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url");
                                Log.i(TAG, uriImage);
                                ImageView imageView = new ImageView(discoverFragment.getActivity());
                                ImageRequest.makeImageRequest(uriImage, discoverFragment.getActivity(), imageView, adapter);
                                discoverMedia.add(new ImageItem(imageView));
                            }
                            discoverFragment.addDiscoverMedia(discoverMedia);
                            pDialog.dismiss();

                        } catch (Exception e) {
                            Log.i(TAG,e.getMessage());
                            e.printStackTrace();
                            pDialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,error.getMessage());
                pDialog.dismiss();
            }
        });

         // Adding request to request queue
        Controller.getInstance(discoverFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);
    }

    /**
     * Get the information of the user IDs of the friends of the user of the application.
     * @param suggestedFriendsFragment Fragment to charge the most popular users images
     * @param accesstoken Access code for the Instagram API
     * @param clientid User ID of the application
     * @param adapter Class name that manage the Fragment Class
     */
    public void getSuggestedFriendsMedia(final SuggestedFriendsFragment suggestedFriendsFragment, final String accesstoken, final String clientid, final SuggestedFriendsAdapter adapter) {

        pDialog = new ProgressDialog(suggestedFriendsFragment.getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        final ArrayList<SuggestedFriends> friends = new ArrayList<>();

        //Request to get the ID of the user's friends
        //https://api.instagram.com/v1/users/{user-id}/followed-by
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/" + clientid + "/followed-by"
                + "/?access_token=" + accesstoken, null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONArray array = response.getJSONArray("data");
                            JSONObject pagination = response.getJSONObject("pagination");

                            for(int i=0; i<array.length(); i++)
                            {
                                String id = array.getJSONObject(i).getString("id");
                                friends.add(new SuggestedFriends(id));
                            }

                            Log.i(TAG, "Getting media inside" + friends.isEmpty());
                            for(final SuggestedFriends f : friends ) {
                                Log.i(TAG, "Getting media from : "+ f.getUsername());
                            }

                        } catch (Exception e) {
                            Log.i(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                        //aqui

                        friendsFromFriends(suggestedFriendsFragment,friends,accesstoken, adapter,clientid);
                        pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, error.getMessage().toString());
            }
        });
        Log.i(TAG, "Getting media outside" + friends.isEmpty());

        // Adding request to request queue
        Controller.getInstance(suggestedFriendsFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);
    }

    /**
     * Function that obtain the friends of my friends
     * The function returns the list of friends that each user ID
     * @param suggestedFriendsFragment Fragment to charge the most popular users images
     * @param array ID list of the user's friends
     * @param accesstoken Access code for the Instagram API
     * @param clientid User ID of the application
     * @param adapter Class name that manage the Fragment Class
     */
    public void friendsFromFriends(final SuggestedFriendsFragment suggestedFriendsFragment, final ArrayList<SuggestedFriends> array, final String accesstoken, final SuggestedFriendsAdapter adapter,  final String clientid){
        for(int i=0; i<4; i++) {

            //Request to get the friends that belongs to each user's friends
            //https://api.instagram.com/v1/users/{user-id}/followed-by
            JsonObjectRequest reqFF = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/" + array.get(i).getId() + "/followed-by"
                    + "/?access_token=" + accesstoken, null,

                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            try {
                                JSONArray arrayFriends = response.getJSONArray("data");
                                JSONObject pagination = response.getJSONObject("pagination");

                                for(int i=0; i<arrayFriends.length(); i++){
                                    String id = arrayFriends.getJSONObject(i).getString("id");
                                    String username = arrayFriends.getJSONObject(i).getString("username");
                                        String fullname = arrayFriends.getJSONObject(i).getString("full_name");
                                        String profile_picture = arrayFriends.getJSONObject(i).getString("profile_picture");
                                        ImageView sugFriendPic = new ImageView(suggestedFriendsFragment.getActivity());
                                        ImageRequest.makeImageRequest(profile_picture, suggestedFriendsFragment.getActivity(), sugFriendPic, adapter);

                                        friendsFriends.add(new SuggestedFriends(username, fullname, id, sugFriendPic));

                                }

                                setSyntheticalLikes(Globals.likesMedia, friendsFriends);
                                try {

                                    String next_max_id = pagination.getString("next_max_id");
                                    Log.i(TAG, next_max_id );
                                    Globals.SUGGESTEDFRIENDS_MEDIA_MAX_ID = next_max_id;
                                }catch (Exception e) {
                                    Log.i(TAG,e.getMessage());
                                    Globals.SUGGESTEDFRIENDS_MEDIA_MAX_ID ="-1";
                                }

                                Log.i(TAG, "Getting media inside" + friendsFriends.isEmpty());
                                for(final SuggestedFriends f : friendsFriends ) {
                                    Log.i(TAG, "Getting media from : "+ f.getUsername());
                                }

                                //Refine the friends to not sugges the same user
                                //repeated friends among user's friends and
                                //repeated friends that are on the user's list of friends
                                for(int i=0; i<friendsFriends.size(); i++){
                                    if(repeatFriends(friendsFriends.get(i).getId(),friendsFriendsFinal) && !friendsFriends.get(i).getId().equals(clientid)
                                            && repeatFriends(friendsFriends.get(i).getId(),array) && canBeFriends(friendsFriends.get(i))){
                                            SuggestedFriends friend = new SuggestedFriends(friendsFriends.get(i).getUsername(), friendsFriends.get(i).getFullname(), friendsFriends.get(i).getId(), friendsFriends.get(i).getProfilepic());
                                            getMedia(friend, accesstoken, suggestedFriendsFragment, adapter);
                                            friendsFriendsFinal.add(friend);
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();


                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String json = null;
                    NetworkResponse response = error.networkResponse;
                    if(response != null && response.data != null){
                        switch(response.statusCode){
                            case 400:
                                json = new String(response.data);
                                Log.e(TAG,json);
                                break;
                        }
                    }
                }
            });

            Log.i(TAG, "Getting media outside" + friendsFriends.isEmpty());

            // Adding request to request queue
            Controller.getInstance(suggestedFriendsFragment.getActivity()).addToRequestQueue(reqFF,
                    tag_json_obj);
        }
    }

    /**
     * Function that permit to identify the repeated users on a list
     * @param value Parameter to diferenciate the user : userID
     * @param finalFriend List of friends with duplicates
     * @return True if there is a repeated value otherwise return False
     */
    public Boolean repeatFriends(String value, ArrayList<SuggestedFriends> finalFriend){
        for(SuggestedFriends suggestedFriends:finalFriend){
            if(suggestedFriends.getId().equals(value)){
                return false;
            }
        }
        return true;
    }

    /**
     * Obtain the three most recent images updated by an expecific user
     * @param suggestedFriends Final suggested Friends
     * @param accesstoken Code to access on the API application
     * @param suggestedFriendsFragment Fragment where the data is updated.
     * @param adapter Control the charge of the Fragment
     */
    private void getMedia(final SuggestedFriends suggestedFriends, String accesstoken, final SuggestedFriendsFragment suggestedFriendsFragment, final SuggestedFriendsAdapter adapter){
            // For each id of the people the user follows retrieve their last pictures
            // when they belong to the current day
            Log.i(TAG, "Getting media from : "+ suggestedFriends.getId());
            //Request of the last media updated on the Instagram account
            //https://api.instagram.com/v1/users/{user-id}/media/recent
            JsonObjectRequest req2 = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/" + suggestedFriends.getId() + "/media/recent"
                    + "/?access_token=" + accesstoken , null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());

                            try {
                                JSONArray array = response.getJSONArray("data");
                                ArrayList<ImageItem> usermedia = new ArrayList<>();

                                //Obtain only the three last images
                                for(int i=0; i<3; i++)
                                {
                                    String uriImage = array.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url");
                                    Log.i(TAG, uriImage);
                                    ImageView imageView = new ImageView(suggestedFriendsFragment.getActivity());
                                    ImageRequest.makeImageRequest(uriImage, suggestedFriendsFragment.getActivity(), imageView, adapter);
                                    usermedia.add(new ImageItem(imageView));
                                }
                                suggestedFriends.setImageItems(usermedia);
                                suggestedFriendsFragment.addSuggestedFriends(suggestedFriends);
                                pDialog.dismiss();

                            } catch (Exception e) {
                                e.printStackTrace();
                                pDialog.dismiss();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try{
                        error.printStackTrace();
                        pDialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();

                    }
                    pDialog.dismiss();
                }

            });
            // Adding request to request queue
            Controller.getInstance(suggestedFriendsFragment.getActivity()).addToRequestQueue(req2,
                    tag_json_obj);
    }

    /**
     * Obtain the media liked by the user
     * @param accesstoken Code to authorize the do a request with an API
     * @param discoverFragment Fragment where the informations is going to be display
     */
    public void requestMediaIDLikes(String accesstoken, DiscoverFragment discoverFragment){

        //Request to get the data that the user likes.
        //https://api.instagram.com/v1/users/self/media/liked
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/self/media/liked"
                + "/?access_token=" + accesstoken, null,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONArray array = response.getJSONArray("data");

                            for(int i=0; i<array.length(); i++)
                            {
                                String id = array.getJSONObject(i).getString("id");
                                Log.i("Request likes", id);
                                Globals.likesMedia.add(id);

                            }

                        } catch (Exception e) {
                            Log.i(TAG,e.getMessage());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,error.getMessage().toString());
            }
        });

        // Adding request to request queue
        Controller.getInstance(discoverFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);

    }

    /**
     * Assign a fake data of likes to the list of the suggested friends
     * that is going to appear on the Discover option on the Instagram
     * @param likes List of media with likes
     * @param suggestedFriends List of suggested friends
     */
    private void setSyntheticalLikes (ArrayList<String> likes, ArrayList<SuggestedFriends> suggestedFriends){
        for(int i=0; i < suggestedFriends.size(); i++){
            ArrayList<String> rndLikes = Utils.getRandomIdList(likes);
            suggestedFriends.get(i).setLikes(rndLikes);
        }
    }

    /**
     * Classify the number of likes permited to be
     * suggested as a friend on the discover list
     * @param suggestedFriend Friend that it is necessary to classify
     * @return True if the user can be suggested as a friend otherwise false
     */
    private Boolean canBeFriends(SuggestedFriends suggestedFriend){
        ArrayList<String> suggestedFriendLikes = suggestedFriend.getLikes();
        int con = 0;

        for(int i=0; i<suggestedFriendLikes.size(); i++){
            for(int j=0; j<Globals.likesMedia.size(); j++){
                if(suggestedFriendLikes.get(i).equals(Globals.likesMedia.get(j))){
                    con += 1;
                }
            }
        }

        if(con > COMMON_LIKES_THRESHOLD){
            return true;
        }
        return false;
    }
}
