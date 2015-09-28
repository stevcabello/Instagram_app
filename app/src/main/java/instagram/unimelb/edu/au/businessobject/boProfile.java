package instagram.unimelb.edu.au.businessobject;


import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import instagram.unimelb.edu.au.adapters.ProfileAdapter;
import instagram.unimelb.edu.au.fragments.ProfileFragment;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.models.Profile;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;

public class boProfile {

    private static String TAG = boProfile.class
            .getSimpleName();
    private String tag_json_obj = "jobj_req";
    //private String tag_json_arry = "jarray_req";

    ProgressDialog pDialog;

    public void getProfileData(final ProfileFragment profileFragment, String accesstoken, String clientid){

        Globals.numberLoads++;

        pDialog = new ProgressDialog(profileFragment.getActivity());
        pDialog.setMessage("Loading...");
        if (Globals.numberLoads <= 5) pDialog.setCancelable(false);
        pDialog.show();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Globals.API_URL + "/users/" + clientid
                        + "/?access_token=" + accesstoken, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {

                            Profile mUserprofile = new Profile();

                            mUserprofile.setId(response.getJSONObject("data").getString("id"));
                            mUserprofile.setUsername(response.getJSONObject("data").getString("username"));
                            mUserprofile.setFullname(response.getJSONObject("data").getString("full_name"));
                            mUserprofile.setProfilepic_url(response.getJSONObject("data").getString("profile_picture"));
                            //mUserprofile.setProfilepic(makeImageRequest(response.getJSONObject("data").getString("profile_picture"), profileFragment.getActivity()));
                            mUserprofile.setNumberposts(response.getJSONObject("data").getJSONObject("counts").getInt("media"));
                            mUserprofile.setNumberfollowers(response.getJSONObject("data").getJSONObject("counts").getInt("followed_by"));
                            mUserprofile.setNumberfollowing(response.getJSONObject("data").getJSONObject("counts").getInt("follows"));

                            profileFragment.addProfileData(mUserprofile);



                        } catch (JSONException e) {
                            Log.i(TAG, e.getMessage());
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG,  error.getMessage());

            }
        }) {

        };

        //pDialog.cancel();

        // Adding request to request queue
        Controller.getInstance(profileFragment.getActivity()).addToRequestQueue(jsonObjReq,
                tag_json_obj);


    }

    public void getProfileMedia(final ProfileFragment profileFragment, String accesstoken, String clientid, final ProfileAdapter adapter) {

        pDialog.show();

        final ArrayList<ImageItem> usermedia = new ArrayList<>();

        // https://api.instagram.com/v1/users/{user-id}/media/recent/?access_token=ACCESS-TOKEN
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,Globals.API_URL + "/users/" + clientid + "/media/recent"
                + "/?access_token=" + accesstoken +"&max_id=" + Globals.PROFILE_MEDIA_MAX_ID, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONArray array = response.getJSONArray("data");
                            JSONObject pagination = response.getJSONObject("pagination");

                            for(int i=0; i<array.length(); i++)
                            {
                                String uriImage = array.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url");
                                Log.i(TAG, uriImage);

                                ImageView imageView = new ImageView(profileFragment.getActivity());
                                ImageRequest.makeImageRequest(uriImage, profileFragment.getActivity(), imageView, adapter);
                                usermedia.add(new ImageItem(imageView));

                                //usermedia.add(new ImageItem(uriImage));
                                //usermedia.add(new ImageItem(ImageRequest.makeImageRequest(uriImage, profileFragment.getActivity(), adapter)));
                            }

                            try {

                                String next_max_id = pagination.getString("next_max_id");
                                //Log.i(TAG, next_max_id);
                                Globals.PROFILE_MEDIA_MAX_ID = next_max_id;
                            }catch (Exception e) {
                                Log.i(TAG,e.getMessage());
                            }

                            profileFragment.addProfileMedia(usermedia);

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
        Controller.getInstance(profileFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);



    }





}
