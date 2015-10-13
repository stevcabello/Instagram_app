package instagram.unimelb.edu.au.businessobject;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import instagram.unimelb.edu.au.networking.Connection.OAuthAuthenticationListener;
import instagram.unimelb.edu.au.networking.Controller;
import instagram.unimelb.edu.au.networking.Session;
import instagram.unimelb.edu.au.utils.Globals;


/**
 * Methods to make request for accesstoken and user profile data
 */
public class boLogin {

    ProgressDialog pdialog;
    private String tag_json_obj = "jobj_req";
    private String TAG = boLogin.class.getSimpleName();

    public void getAccessToken(Context context, final String code, final String clientid, final String clientsecret,
                               final String callbackurl , final Session session,
                               final OAuthAuthenticationListener oAuthAuthenticationListener) {

        pdialog = new ProgressDialog(context);
        pdialog.setMessage("Connecting to Instagram ...");
        pdialog.show();

        JSONObject obj =null;

        //Define the post params
        try {
            obj = new JSONObject();
            obj.put("client_id", clientid );
            obj.put("client_secret",clientsecret);
            obj.put("grant_type","authorization_code");
            obj.put("redirect_uri",callbackurl);
            obj.put("code",code);

        }catch (JSONException e){
            Log.i(TAG,"Error creating post params");
        }

        StringRequest req = new StringRequest(Request.Method.POST,Globals.TOKEN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response2) {

                        try{
                            JSONObject response = new JSONObject(response2);
                            String accestoken = response.getString("access_token");
                            Log.i(TAG, "access token: " + accestoken);

                            String id = response.getJSONObject("user").getString("id");
                            Log.i(TAG,"client id: " + id);
                            String username = response.getJSONObject("user").getString("username");
                            String fullname = response.getJSONObject("user").getString("full_name");

                            Globals.profile.setId(id);
                            Globals.profile.setUsername(username);
                            Globals.profile.setFullname(fullname);

                            session.storeAccessToken(accestoken, id, username, fullname);

                            oAuthAuthenticationListener.onSuccess();
                            pdialog.dismiss();

                        }catch (JSONException ex){
                            ex.printStackTrace();
                            oAuthAuthenticationListener.onFail("Failed to get accesstoken");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       error.printStackTrace();
                       pdialog.dismiss();
                    }
                 })//;
        {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("client_id", clientid);
                params.put("client_secret", clientsecret);
                params.put("grant_type", "authorization_code");
                params.put("redirect_uri", callbackurl);
                params.put("code", code);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };



        // Adding request to request queue
        Controller.getInstance(context).addToRequestQueue(req,
                tag_json_obj);

    }


    public void getUserProfileData(Context context,final Session session, final OAuthAuthenticationListener oAuthAuthenticationListener) {

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,Globals.API_URL
                + "/users/" + session.getId() + "/?access_token=" + session.getAccessToken(),null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try{

                            JSONObject data = response.getJSONObject("data");

                            String profile_pic_url = data.getString("profile_picture");
                            int numberposts = data.getJSONObject("counts").getInt("media");
                            int numberfollowers = data.getJSONObject("counts").getInt("followed_by");
                            int numberfollowing = data.getJSONObject("counts").getInt("follows");

                            Globals.profile.setProfilepic_url(profile_pic_url);
                            Globals.profile.setNumberposts(numberposts);
                            Globals.profile.setNumberfollowers(numberfollowers);
                            Globals.profile.setNumberfollowing(numberfollowing);


                        }catch (JSONException ex){
                            ex.printStackTrace();
                            oAuthAuthenticationListener.onFail("Failed to get user information");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });


        // Adding request to request queue
        Controller.getInstance(context).addToRequestQueue(req,
                tag_json_obj);

    }




}
