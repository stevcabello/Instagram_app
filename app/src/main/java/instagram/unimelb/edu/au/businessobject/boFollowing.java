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
import instagram.unimelb.edu.au.models.FollowingActivityFeed;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;
import instagram.unimelb.edu.au.utils.Utils;

/**
 * Created by Angela on 9/21/2015.
 */
public class boFollowing {
    private static String TAG = boFollowing.class
            .getSimpleName();
    private String tag_json_obj = "jobj_req";
    //private String tag_json_arry = "jarray_req";

    ProgressDialog pDialog;

    public void getProfileMedia(final FollowingActivityFeedFragment followingActivityFragment, final String accesstoken, String clientid, final FollowingActivityFeedAdapter adapter) {
        pDialog = new ProgressDialog(followingActivityFragment.getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        final ArrayList<FollowingActivityFeed> followingActivity = new ArrayList<>();

        // https://api.instagram.com/v1/users/{user-id}/media/recent/?access_token=ACCESS-TOKEN
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/" + clientid + "/follows"
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

                                String username = array.getJSONObject(i).getString("username");
                                String fullname = array.getJSONObject(i).getString("full_name");
                                String id = array.getJSONObject(i).getString("id");
                                String profile_picture = array.getJSONObject(i).getString("profile_picture");
                                followingActivity.add(new FollowingActivityFeed(username, fullname, id, profile_picture ));
                            }
                            try {

                                String next_max_id = pagination.getString("next_max_id");
                                Log.i(TAG, next_max_id );
                                Globals.FOLLOWERACTIVITY_MEDIA_MAX_ID = next_max_id;
                            }catch (Exception e) {
                                Log.i(TAG,e.getMessage());
                                Globals.FOLLOWERACTIVITY_MEDIA_MAX_ID ="-1";
                            }

                            Log.i(TAG, "Getting media inside" + followingActivity.isEmpty());
                            for(final FollowingActivityFeed f : followingActivity ) {
                                Log.i(TAG, "Getting media from : "+ f.getUsername());
                            }
                            getMedia(followingActivity,accesstoken,followingActivityFragment,adapter);
                          //  followingActivityFragment.addProfileMedia(followingActivity);

                            pDialog.dismiss();

                        } catch (Exception e) {
                            Log.i(TAG,e.getMessage());
                            e.printStackTrace();
                        }
                        //aqui



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,error.getMessage().toString());
            }
      });
        Log.i(TAG, "Getting media outside" + followingActivity.isEmpty());

        // Adding request to request queue
        Controller.getInstance(followingActivityFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);






    }


    //
    private void getMedia( final ArrayList<FollowingActivityFeed> followingActivity, String accesstoken, final FollowingActivityFeedFragment followingActivityFragment, final FollowingActivityFeedAdapter adapter){
        // For each id of the people the user follows retrieve their last pictures when they belong to the current day
        for(final FollowingActivityFeed f : followingActivity ){
            Log.i(TAG, "Getting media from : "+ f.getUsername());
            JsonObjectRequest req2 = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/" + f.getId() + "/media/recent"
                    + "/?access_token=" + accesstoken , null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());

                            try {
                                JSONArray array = response.getJSONArray("data");
                                ArrayList<ImageItem> usermedia = new ArrayList<>();
                                boolean hasRecentMedia = false;
                                for(int i=0; i<array.length(); i++)
                                {
                                    String datetime= Utils.getElapsedtime(array.getJSONObject(i).getString("created_time"),"short");
                                    String unit = datetime.substring(datetime.length() - 1);
                                    if(unit.equals("h") || unit.equals("s") || unit.equals("m") ) {
                                        String uriImage = array.getJSONObject(i).getJSONObject("images").getJSONObject("thumbnail").getString("url");
                                        Log.i(TAG, uriImage);
                                        ImageView imageView = new ImageView(followingActivityFragment.getActivity());
                                        ImageRequest.makeImageRequest(uriImage, followingActivityFragment.getActivity(), imageView, adapter);
                                        usermedia.add(new ImageItem(imageView));
                                        hasRecentMedia = true;
                                    }
                                }
                                if (hasRecentMedia==true) {
                                    f.setImageItems(usermedia);
                                    Log.i(TAG, f.getUrlprofilepic());
                                    ImageView imageView = new ImageView(followingActivityFragment.getActivity());
                                    ImageRequest.makeImageRequest(f.getUrlprofilepic(), followingActivityFragment.getActivity(), imageView, adapter);
                                    f.setProfilepic(imageView);
                                    followingActivityFragment.addProfileMedia(f);

                                }

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
            Controller.getInstance(followingActivityFragment.getActivity()).addToRequestQueue(req2,
                    tag_json_obj);

        }


    }


}
