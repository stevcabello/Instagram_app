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

import instagram.unimelb.edu.au.adapters.DiscoverAdapter;
import instagram.unimelb.edu.au.adapters.FollowingActivityFeedAdapter;
import instagram.unimelb.edu.au.adapters.SubSuggestedFriendsAdapter;
import instagram.unimelb.edu.au.fragments.DiscoverFragment;
import instagram.unimelb.edu.au.fragments.FollowingActivityFeedFragment;
import instagram.unimelb.edu.au.fragments.SuggestedFriendsFragment;
import instagram.unimelb.edu.au.models.FollowingActivityFeed;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.models.SuggestedFriends;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Created by Carina on 19/09/15.
 */
public class boDiscover {
    private String TAG = boDiscover.class
            .getSimpleName();
    private String tag_json_obj = "jobj_req";
    ProgressDialog pDialog;


    public void getDiscoverMedia(final DiscoverFragment discoverFragment, String accesstoken, String clientid, final DiscoverAdapter adapter) {

        Globals.numberLoads++;

        pDialog = new ProgressDialog(discoverFragment.getActivity());
        pDialog.setMessage("Loading...");
        if (Globals.numberLoads <= 5) pDialog.setCancelable(false);
        pDialog.show();

        final ArrayList<ImageItem> discoverMedia = new ArrayList<>();

        // https://api.instagram.com/v1/users/{user-id}/media/recent/?access_token=ACCESS-TOKEN
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

    public void getSuggestedFriendsMedia(final SuggestedFriendsFragment suggestedFriendsFragment, final String accesstoken, String clientid, final SubSuggestedFriendsAdapter adapter) {

        Globals.numberLoads++;

        pDialog = new ProgressDialog(suggestedFriendsFragment.getActivity());
        pDialog.setMessage("Loading...");
        if (Globals.numberLoads <= 5) pDialog.setCancelable(false);
        pDialog.show();

        final ArrayList<SuggestedFriends> friends = new ArrayList<>();

        // https://api.instagram.com/v1/users/{user-id}/media/recent/?access_token=ACCESS-TOKEN
        ///v1/users/2108719533/followed-by?access_token=
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/" + clientid + "/media/recent"
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

                                String username = array.getJSONObject(i).getString("username");
                                String fullname = array.getJSONObject(i).getString("full_name");
                                String id = array.getJSONObject(i).getString("id");
                                String profile_picture = array.getJSONObject(i).getString("profile_picture");
                                friends.add(new SuggestedFriends(username, fullname, id, profile_picture ));


                            }
                            try {

                                String next_max_id = pagination.getString("next_max_id");
                                Log.i(TAG, next_max_id );
                                Globals.FRIENDS_MEDIA_MAX_ID = next_max_id;
                            }catch (Exception e) {
                                Log.i(TAG,e.getMessage());
                                Globals.FRIENDS_MEDIA_MAX_ID ="-1";
                            }

                            Log.i(TAG, "Getting media inside" + friends.isEmpty());
                            for(final SuggestedFriends f : friends ) {
                                Log.i(TAG, "Getting media from : "+ f.getUsername());
                            }
                            //getMedia(friends, accesstoken, suggestedFriendsFragment, adapter);
                            //  followingActivityFragment.addProfileMedia(followingActivity);

                            //  pDialog.dismiss();

                        } catch (Exception e) {
                            Log.i(TAG, e.getMessage());
                            e.printStackTrace();
                        }
                        //aqui



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





}
