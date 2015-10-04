package instagram.unimelb.edu.au.businessobject;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import instagram.unimelb.edu.au.fragments.UserFeedFragment;
import instagram.unimelb.edu.au.models.Comments;
import instagram.unimelb.edu.au.models.Likes;
import instagram.unimelb.edu.au.models.UserFeed;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Manages communication with Instagram API to get user feed data
 */
public class boUserFeed {

    private static String TAG = boUserFeed.class
            .getSimpleName();
    private String tag_json_obj = "jobj_req";
    ProgressDialog pDialog;


    /**
     * Get user feed data from Instagram API
     *
     * @param userFeedFragment
     * @param accesstoken
     * @param adapter
     */
    public void getUserFeedData(final UserFeedFragment userFeedFragment, String accesstoken, final ArrayAdapter adapter, final int sortBy) {

        Globals.numberLoads++;

        pDialog = new ProgressDialog(userFeedFragment.getActivity());
        pDialog.setMessage("Loading...");
        if (Globals.numberLoads <= 5) pDialog.setCancelable(false);
        pDialog.show();



        final ArrayList<UserFeed> arrayUserfeed = new ArrayList<UserFeed>();

        // https://api.instagram.com/v1/users/self/feed?access_token=ACCESS-TOKEN
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/self"
                + "/feed?access_token=" + accesstoken +"&max_id=" + Globals.USERFEED_MAX_ID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("uriimages", response.toString());

                        try {

                            JSONArray array = response.getJSONArray("data");
                            JSONObject pagination = response.getJSONObject("pagination");


                            for(int i=0; i<array.length(); i++)
                            {

                                UserFeed mUserfeed = new UserFeed();

                                mUserfeed.setMedia_id(array.getJSONObject(i).getString("id"));
                                Log.i(TAG,array.getJSONObject(i).getString("id"));

                                String profilepic_url = array.getJSONObject(i).getJSONObject("user").getString("profile_picture");
                                mUserfeed.setProfilepic_url(profilepic_url);
                                ImageView profilepic = new ImageView(userFeedFragment.getActivity());
                                ImageRequest.makeImageRequest(profilepic_url, userFeedFragment.getActivity(), profilepic, adapter);
                                mUserfeed.setProfilepic(profilepic);

                                mUserfeed.setUsername(array.getJSONObject(i).getJSONObject("user").getString("username"));
                                mUserfeed.setCreated_time(array.getJSONObject(i).getString("created_time"));

                                String photo_url = array.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                                Log.i("uriimages", "total =" + String.valueOf(array.length()));
                                Log.i("uriimages", photo_url);
                                mUserfeed.setPhoto_url(photo_url);
                                ImageView photo = new ImageView(userFeedFragment.getActivity());
                                ImageRequest.makeImageRequest(photo_url, userFeedFragment.getActivity(), photo, adapter);
                                mUserfeed.setPhoto(photo);

                                mUserfeed.setType(array.getJSONObject(i).getString("type"));
                                mUserfeed.setNumLikes(array.getJSONObject(i).getJSONObject("likes").getInt("count"));


                                try {
                                    mUserfeed.setDescription(array.getJSONObject(i).getJSONObject("caption").getString("text"));
                                }catch (Exception e){
                                    mUserfeed.setDescription("");
                                }



                                mUserfeed.setNumcomments(array.getJSONObject(i).getJSONObject("comments").getInt("count"));

                                try {
                                    mUserfeed.setLatitude((double) array.getJSONObject(i).getJSONObject("location").getLong("latitude"));
                                    mUserfeed.setLongitude((double) array.getJSONObject(i).getJSONObject("location").getLong("longitude"));
                                    mUserfeed.setLocation(array.getJSONObject(i).getJSONObject("location").getString("name"));
                                }catch (JSONException e){
                                    Log.i(TAG,e.getMessage());
//                                    mUserfeed.setLatitude(0.00);
//                                    mUserfeed.setLongitude(0.00);
//                                    mUserfeed.setLocation("");
                                }

                                //Log.i("uriimages",mUserfeed.getUsername());
                               // Log.i("uriimages",mUserfeed.getLocation());
                                JSONArray arrayComments = array.getJSONObject(i).getJSONObject("comments").getJSONArray("data");
                                ArrayList<Comments> comments = new ArrayList<Comments>();

                                // The comments from the userfeed's photo uploaded are stored into a Comment's array.
                                for (int j=0; j<arrayComments.length(); j++){
                                    String commentuser = arrayComments.getJSONObject(j).getJSONObject("from").getString("username");
                                    String commenttext = arrayComments.getJSONObject(j).getString("text");
                                    String commentcreatedtime = arrayComments.getJSONObject(j).getString("created_time");

                                    String commentprofilepic_url = arrayComments.getJSONObject(j).getJSONObject("from").getString("profile_picture");
                                    ImageView commentprofilepic = new ImageView(userFeedFragment.getActivity());
                                    ImageRequest.makeImageRequest(commentprofilepic_url, userFeedFragment.getActivity(), commentprofilepic, adapter);

                                    Comments comment = new Comments(commentuser,commenttext,commentcreatedtime,commentprofilepic,commentprofilepic_url);
                                    comments.add(comment);
                                }

                                mUserfeed.setComments(comments);


                                JSONArray arrayLikes = array.getJSONObject(i).getJSONObject("likes").getJSONArray("data");
                                ArrayList<Likes> likes = new ArrayList<Likes>();

                                // The likes from the userfeed's photo uploaded are stored into a Like's array.
                                for (int j=0; j<arrayLikes.length(); j++){
                                    String likeusername = arrayLikes.getJSONObject(j).getString("username");
                                    String likeuserfullname = arrayLikes.getJSONObject(j).getString("full_name");

                                    String likeprofilepic_url = arrayLikes.getJSONObject(j).getString("profile_picture");
                                    ImageView likeprofilepic = new ImageView(userFeedFragment.getActivity());
                                    ImageRequest.makeImageRequest(likeprofilepic_url, userFeedFragment.getActivity(),likeprofilepic,adapter);

                                    Likes like = new Likes(likeusername,likeuserfullname,likeprofilepic,likeprofilepic_url);
                                    likes.add(like);
                                }

                                mUserfeed.setLikes(likes);

                                //add the each data from the user feed into an user feed's array/
                                arrayUserfeed.add(mUserfeed);

                            }

                            try {
                                String next_max_id = pagination.getString("next_max_id");
                                Globals.USERFEED_MAX_ID = next_max_id;
                                Log.i("uriimages", "actualizp =" + Globals.USERFEED_MAX_ID);
                            }catch (Exception e) {
                                Log.i(TAG,e.getMessage());
                            }


                            //The user feed array is sent to user feed fragment.
                            userFeedFragment.addUserFeedData(arrayUserfeed,sortBy);

                            pDialog.dismiss();

                        } catch (Exception e) {
                            //Log.i(TAG,e.getMessage());
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
        Controller.getInstance(userFeedFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);

    }

}
