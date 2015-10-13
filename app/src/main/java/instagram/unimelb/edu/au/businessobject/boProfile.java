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

import instagram.unimelb.edu.au.adapters.ProfileAdapter;
import instagram.unimelb.edu.au.fragments.ProfileFragment;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Class of the client profile
 */
public class boProfile {

    private static String TAG = boProfile.class.getSimpleName();
    private String tag_json_obj = "jobj_req";


    ProgressDialog pDialog;



    /**
     * Request of profile media for the authenticated user
     *
     * @param profileFragment Fragment of the user's profile
     * @param accesstoken Access_token of the application
     * @param clientid Id of the authenticated user
     * @param adapter Adapter that handles the gridview of medias of authenticated user
     */
    public void getProfileMedia(final ProfileFragment profileFragment, String accesstoken, String clientid, final ProfileAdapter adapter) {

        pDialog = new ProgressDialog(profileFragment.getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        final ArrayList<ImageItem> usermedia = new ArrayList<>();

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
                            }

                            try {
                                String next_max_id = pagination.getString("next_max_id");
                                Globals.PROFILE_MEDIA_MAX_ID = next_max_id;
                            }catch (Exception e) {
                                Log.i(TAG,e.getMessage());
                            }

                            profileFragment.addProfileMedia(usermedia);

                            pDialog.dismiss();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }




        });




        // Adding request to request queue
        Controller.getInstance(profileFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);



    }



}
