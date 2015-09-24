package instagram.unimelb.edu.au.businessobject;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import instagram.unimelb.edu.au.fragments.CommentsFragment;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Handles leaving of comments to Instagram API
 */
public class boComments {

    private static String TAG = boComments.class
            .getSimpleName();
    private String tag_json_obj = "jobj_req";
    ProgressDialog pDialog;



    public void sendComment(final CommentsFragment commentsFragment, String accesstoken, String media_id, final String text ,final ArrayAdapter adapter) {

        pDialog = new ProgressDialog(commentsFragment.getActivity());
        pDialog.setMessage("Sending comment...");
        pDialog.show();


        //Example post comment request
        //curl -F 'access_token=ACCESS-TOKEN' \
        //-F 'text=This+is+my+comment' \
        //https://api.instagram.com/v1/media/{media-id}/comments

        JSONObject obj =null;

        //Define the post params
        try {
            obj = new JSONObject();
            obj.put("text", text );

        }catch (JSONException e){
            Log.i(TAG,"Error creating post params");
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,Globals.API_URL + "/media/" + media_id
                + "/comments?access_token=" + accesstoken,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Never succeeds the request due to Instagram API authorization issue
                        Toast.makeText(commentsFragment.getActivity(), "Leaving comment succeed!",Toast.LENGTH_SHORT).show();
                        pDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        String json = new String(networkResponse.data);

                        JSONObject obj = null;
                        String jsonErr = null;
                        try {
                            obj = new JSONObject(json);
                            jsonErr = obj.getJSONObject("meta").getString("error_message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Toast.makeText(commentsFragment.getActivity(), "Leaving comment fails due to: " + jsonErr , Toast.LENGTH_LONG).show();

                        //Simulates the update of the comment in the UI
                        commentsFragment.simulateLeavingComment(text);

                        pDialog.dismiss();
                    }
                });


        // Adding request to request queue
        Controller.getInstance(commentsFragment.getActivity()).addToRequestQueue(req,
                tag_json_obj);

    }


}
