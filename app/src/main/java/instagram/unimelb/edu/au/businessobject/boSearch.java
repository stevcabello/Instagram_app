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

import instagram.unimelb.edu.au.adapters.SearchAdapter;
import instagram.unimelb.edu.au.fragments.SearchFragment;
import instagram.unimelb.edu.au.models.Search;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Class that handle the search of user profiles
 */
public class boSearch {
    private static String TAG = boSearch.class
            .getSimpleName();
    private String tag_json_obj = "jobj_req";

    ProgressDialog pDialog;

    /**
     * Method that search a profile
     * @param searchFragment
     * @param accesstoken
     * @param query
     * @param adapter
     */
    public void getSearch(final SearchFragment searchFragment, String accesstoken, String query, final SearchAdapter adapter) {

        pDialog = new ProgressDialog(searchFragment.getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        final ArrayList<Search> searches = new ArrayList<>();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, Globals.API_URL + "/users/search?q=" + query
                + "&access_token=" + accesstoken , null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONArray array = response.getJSONArray("data");
                           // JSONObject pagination = response.getJSONObject("pagination");

                            for(int i=0; i<array.length(); i++)
                            {
                                String profilePicture = array.getJSONObject(i).getString("profile_picture");
                                String username = array.getJSONObject(i).getString("username");
                                String fullName = array.getJSONObject(i).getString("full_name");
                                Log.i(TAG, profilePicture);
                                ImageView imageView = new ImageView(searchFragment.getActivity());
                                ImageRequest.makeImageRequest(profilePicture, searchFragment.getActivity(), imageView, adapter);
                                searches.add(new Search(username,fullName,imageView));

                            }

                            searchFragment.addProfileMedia(searches);
                            pDialog.dismiss();

                        } catch (Exception e) {
                            //Log.i(TAG,e.getMessage());
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


        // Adding request to request queue
        Controller.getInstance(searchFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);



    }
}
