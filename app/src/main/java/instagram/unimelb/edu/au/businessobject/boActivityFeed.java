package instagram.unimelb.edu.au.businessobject;
/**
 * Class that handles the activity of the user.
 */

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import instagram.unimelb.edu.au.adapters.YouActivityFeedAdapter;
import instagram.unimelb.edu.au.fragments.YouActivityFeedFragment;
import instagram.unimelb.edu.au.models.YouActivityFeed;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;

public class boActivityFeed {
    private String TAG = boActivityFeed.class
            .getSimpleName();
    private String tag_json_obj = "jobj_req";

    ProgressDialog pDialog;

    /**
     * Method that retrieve the information of likes and comments from the active user
     * @param youActivityFeedFragment
     * @param accesstoken
     * @param clientid
     * @param adapter
     */
    public void getProfileMedia(final YouActivityFeedFragment youActivityFeedFragment, final String accesstoken, final String clientid,final YouActivityFeedAdapter adapter) {
        pDialog = new ProgressDialog(youActivityFeedFragment.getActivity());
        pDialog.setMessage("Loading...");

        pDialog.show();

        final ArrayList<YouActivityFeed> userActivity = new ArrayList<>();
        // https://api.instagram.com/v1/users/{user-id}/media/recent/?access_token=ACCESS-TOKEN
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,Globals.API_URL + "/users/" + clientid + "/media/recent"
                + "/?access_token=" + accesstoken +"&max_id=" + Globals.YOUACTIVITY_MEDIA_MAX_ID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        YouActivityFeed.typeActivity type= YouActivityFeed.typeActivity.LIKE;
                        String comment= "";
                        String username="";
                        String profilePicture="";
                        String timePublication ="";
                        Date datePublished = new Date();
                        ImageView imageView;
                        ImageView imageProfileView;
                        try {
                            JSONArray array = response.getJSONArray("data");
                            JSONObject pagination = response.getJSONObject("pagination");

                            for(int i=0; i<array.length(); i++)
                            {
                                comment= "";
                                timePublication = array.getJSONObject(i).getString("created_time");
                                String uriImage = array.getJSONObject(i).getJSONObject("images").getJSONObject("thumbnail").getString("url");
                                imageView = new ImageView(youActivityFeedFragment.getActivity());
                                ImageRequest.makeImageRequest(uriImage, youActivityFeedFragment.getActivity(), imageView, adapter);
                                int count = Integer.parseInt(array.getJSONObject(i).getJSONObject("likes").getString("count"));
                                Log.i(TAG, Integer.toString(count));
                                if (count > 0) {
                                    JSONArray arrayLikes = array.getJSONObject(i).getJSONObject("likes").getJSONArray("data");
                                    for(int l=0; l<arrayLikes.length(); l++){
                                       if (arrayLikes.getJSONObject(l).getString("id")!=clientid) {
                                           username = arrayLikes.getJSONObject(l).getString("username");
                                           profilePicture = arrayLikes.getJSONObject(l).getString("profile_picture");
                                           imageProfileView = new ImageView(youActivityFeedFragment.getActivity());
                                           ImageRequest.makeImageRequest(profilePicture, youActivityFeedFragment.getActivity(), imageProfileView, adapter);
                                           type = YouActivityFeed.typeActivity.LIKE;
                                           userActivity.add(new YouActivityFeed(username, imageProfileView, imageView, type, datePublished, comment, timePublication));
                                       }
                                    }
                                }

                                int countComments = Integer.parseInt(array.getJSONObject(i).getJSONObject("comments").getString("count"));
                                Log.i(TAG, Integer.toString(countComments));
                                if (countComments > 0) {
                                    JSONArray arrayComments = array.getJSONObject(i).getJSONObject("comments").getJSONArray("data");
                                    for(int c=0; c<arrayComments.length(); c++){
                                        type =YouActivityFeed.typeActivity.COMMENT;
                                        comment = arrayComments.getJSONObject(c).getString("text");
                                        timePublication = arrayComments.getJSONObject(c).getString("created_time");
                                        username = arrayComments.getJSONObject(c).getJSONObject("from").getString("username");
                                        profilePicture =arrayComments.getJSONObject(c).getJSONObject("from").getString("profile_picture");
                                        imageProfileView = new ImageView(youActivityFeedFragment.getActivity());
                                        ImageRequest.makeImageRequest(profilePicture, youActivityFeedFragment.getActivity(), imageProfileView, adapter);
                                        userActivity.add(new YouActivityFeed(username, imageProfileView, imageView, type, datePublished, comment, timePublication));
                                    }
                                }
                            }

                            try {

                                String next_max_id = pagination.getString("next_max_id");
                                Globals.YOUACTIVITY_MEDIA_MAX_ID = next_max_id;
                            }catch (Exception e) {
                                Log.i(TAG,e.getMessage());
                                Globals.YOUACTIVITY_MEDIA_MAX_ID = "-1";
                            }

                            getFollowedby(userActivity,youActivityFeedFragment,accesstoken,clientid,adapter);
                            pDialog.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,error.getMessage());
            }

        });
        // Adding request to request queue
        Controller.getInstance(youActivityFeedFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);
    }

    /**
     * Method that retrieves the list of followers of a given user
     * @param userActivity
     * @param youActivityFeedFragment
     * @param accesstoken
     * @param clientid
     * @param adapter
     */
    public void getFollowedby(final ArrayList<YouActivityFeed> userActivity, final YouActivityFeedFragment youActivityFeedFragment, String accesstoken, String clientid,final YouActivityFeedAdapter adapter) {

        pDialog = new ProgressDialog(youActivityFeedFragment.getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

       // final ArrayList<YouActivityFeed> followedActivity = new ArrayList<>();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/" + clientid + "/followed-by"
                + "/?access_token=" + accesstoken, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        try {
                            JSONArray array = response.getJSONArray("data");
                           // JSONObject pagination = response.getJSONObject("pagination");
                            ImageView imageProfileView;
                            for(int i= Globals.FOLLOWEDBY_MEDIA_MAX_ID; i< Globals.FOLLOWEDBY_MEDIA_MAX_ID+2; i++)
                            {
                                String username = array.getJSONObject(i).getString("username");
                                String profilePicture = array.getJSONObject(i).getString("profile_picture");
                                imageProfileView = new ImageView(youActivityFeedFragment.getActivity());
                                ImageRequest.makeImageRequest(profilePicture, youActivityFeedFragment.getActivity(), imageProfileView, adapter);
                                userActivity.add(new YouActivityFeed(username, imageProfileView, YouActivityFeed.typeActivity.FOLLOW));
                            }


                            Globals.FOLLOWEDBY_MEDIA_MAX_ID =  Globals.FOLLOWEDBY_MEDIA_MAX_ID+2;;

                            youActivityFeedFragment.addYouActivity(userActivity);

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
        Controller.getInstance(youActivityFeedFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);

    }


}
