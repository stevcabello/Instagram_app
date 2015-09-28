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
import instagram.unimelb.edu.au.fragments.DiscoverFragment;
import instagram.unimelb.edu.au.models.ImageItem;
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

//TODO: Do we need this???? ... the method make imagerequest was deleted from here because it already exists in ImageRequest class
//    public void getDiscoverData(final DiscoverFragment discoverFragment, String accesstoken, String clientid){
//        pDialog = new ProgressDialog(discoverFragment.getActivity());
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
//                Globals.API_URL + "/users/" + clientid
//                        + "/?access_token=" + accesstoken, null,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG, response.toString());
//
//                        try {
//
//                            Profile mDiscoverProfile = new Profile();
//                            Discover mDiscover = new Discover();
//
//                            mDiscoverProfile.setId(response.getJSONObject("data").getString("id"));
//                            mDiscoverProfile.setUsername(response.getJSONObject("data").getString("username"));
//                            mDiscoverProfile.setFullname(response.getJSONObject("data").getString("full_name"));
//                            mDiscoverProfile.setProfilepic(makeImageRequest(response.getJSONObject("data").getString("profile_picture"),
//                                    discoverFragment.getActivity()));
//
//                            mDiscover.setProfileOwner(mDiscoverProfile);
//                            //mDiscoverFriend.setProfileDiscover();
//                            //TODO: Anadir lista de sugerencias
//
//                            discoverFragment.addDiscoverData(mDiscoverProfile);
//
//                        } catch (JSONException e) {
//                            Log.i(TAG, e.getMessage());
//                        }
//
//
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i(TAG,  error.getMessage());
//
//            }
//        }) {
//
//        };
//
//        // Adding request to request queue
//        Controller.getInstance(discoverFragment.getActivity()).addToRequestQueue(jsonObjReq,
//                tag_json_obj);
//    }






}
