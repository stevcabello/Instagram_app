package instagram.unimelb.edu.au.businessobject;

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

import instagram.unimelb.edu.au.adapters.FollowingActivityFeedAdapter;
import instagram.unimelb.edu.au.fragments.FollowingActivityFeedFragment;
import instagram.unimelb.edu.au.models.FollowerActivityFeed;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Created by Angela on 9/21/2015.
 */
public class boFollowers {
    private static String TAG = boFollowers.class
            .getSimpleName();
    private String tag_json_obj = "jobj_req";
    //private String tag_json_arry = "jarray_req";

    ProgressDialog pDialog;

    public void getProfileMedia(final FollowingActivityFeedFragment followingActivityFragment, String accesstoken, String clientid, final FollowingActivityFeedAdapter adapter) {
        pDialog = new ProgressDialog(followingActivityFragment.getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        final ArrayList<FollowerActivityFeed> followersActivity = new ArrayList<>();

        // https://api.instagram.com/v1/users/{user-id}/media/recent/?access_token=ACCESS-TOKEN
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/" + clientid + "/followed-by"
                + "/?access_token=" + accesstoken +"&max_id=" + Globals.FOLLOWERACTIVITY_MEDIA_MAX_ID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONArray array = response.getJSONArray("data");
                            JSONObject pagination = response.getJSONObject("pagination");

                            for(int i=0; i<array.length(); i++)
                            {
                                String uriImage = array.getJSONObject(i).getString("profile_picture");
                                Log.i(TAG, uriImage);
                                String username = array.getJSONObject(i).getString("username");
                                Log.i(TAG, username);
                                String fullname = array.getJSONObject(i).getString("full_name");
                                Log.i(TAG, fullname);

                                ImageView imageView = new ImageView(followingActivityFragment.getActivity());
                                ImageRequest.makeImageRequest(uriImage, followingActivityFragment.getActivity(), imageView, adapter);
                                followersActivity.add(new FollowerActivityFeed(imageView,username,fullname));

                                //usermedia.add(new ImageItem(uriImage));
                                //usermedia.add(new ImageItem(ImageRequest.makeImageRequest(uriImage, profileFragment.getActivity(), adapter)));
                            }

                            try {

                                String next_max_id = pagination.getString("next_max_id");
                                //Log.i(TAG, next_max_id);
                                Globals.FOLLOWERACTIVITY_MEDIA_MAX_ID = next_max_id;
                            }catch (Exception e) {
                                Log.i(TAG,e.getMessage());
                                Globals.FOLLOWERACTIVITY_MEDIA_MAX_ID ="-1";
                            }

                            followingActivityFragment.addProfileMedia(followersActivity);

                            pDialog.dismiss();

                        } catch (Exception e) {
                            //Log.i(TAG,e.getMessage());
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
        Controller.getInstance(followingActivityFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);



    }



}
