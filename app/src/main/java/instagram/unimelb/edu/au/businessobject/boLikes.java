package instagram.unimelb.edu.au.businessobject;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Handles sending post request of likes to Instagram API
 */
public class boLikes {

    private static String TAG = boLikes.class.getSimpleName();
    private String tag_json_obj = "jobj_req";
    ProgressDialog pDialog;


    /**
     * Send like to some other post
     *
     * @param context context for the fragment
     * @param accesstoken access_token of the application
     * @param media_id id of the posted image/photo
     */
    public void sendLike(final Context context, String accesstoken, String media_id) {

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Giving like...");
        pDialog.show();


        JSONObject obj =null;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,Globals.API_URL + "/media/" + media_id
                + "/likes?access_token=" + accesstoken,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Never succeeds the request due to Instagram API authorization issue
                        Toast.makeText(context, "Giving like succeeds!",Toast.LENGTH_SHORT).show();
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

                        //Showing the error of authorization received
                        Toast toast = Toast.makeText(context, "Giving like fails due to: " + jsonErr , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                        toast.show();

                        pDialog.dismiss();
                    }
                });


        // Adding request to request queue
        Controller.getInstance(context).addToRequestQueue(req,
                tag_json_obj);

    }


    /**
     * Deletes a given like
     *
     * @param context Context of the Likes Fragment
     * @param accesstoken Access_token of the application
     * @param media_id Id of the posted image/photo
     */
    public void deleteLike(final Context context, String accesstoken, String media_id) {

        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Deleting like...");
        pDialog.show();


        JSONObject obj =null;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE,Globals.API_URL + "/media/" + media_id
                + "/likes?access_token=" + accesstoken,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Never succeeds the request due to Instagram API authorization issue
                        Toast.makeText(context, "Deleting like succeeds!",Toast.LENGTH_SHORT).show();
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

                        //Showing the error of authorization received
                        Toast toast = Toast.makeText(context, "Deleting like fails due to: " + jsonErr , Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();

                        pDialog.dismiss();
                    }
                });


        // Adding request to request queue
        Controller.getInstance(context).addToRequestQueue(req,
                tag_json_obj);

    }



}
