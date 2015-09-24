package instagram.unimelb.edu.au.businessobject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import instagram.unimelb.edu.au.adapters.DiscoverAdapter;
import instagram.unimelb.edu.au.fragments.DiscoverFragment;
import instagram.unimelb.edu.au.models.Discover;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.models.Profile;
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
    private String tag_json_arry = "jarray_req";
    ProgressDialog pDialog;

    public void getDiscoverData(final DiscoverFragment discoverFragment, String accesstoken, String clientid){
        pDialog = new ProgressDialog(discoverFragment.getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Globals.API_URL + "/users/" + clientid
                        + "/?access_token=" + accesstoken, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {

                            Profile mDiscoverProfile = new Profile();
                            Discover mDiscover = new Discover();

                            mDiscoverProfile.setId(response.getJSONObject("data").getString("id"));
                            mDiscoverProfile.setUsername(response.getJSONObject("data").getString("username"));
                            mDiscoverProfile.setFullname(response.getJSONObject("data").getString("full_name"));
                            mDiscoverProfile.setProfilepic(makeImageRequest(response.getJSONObject("data").getString("profile_picture"),
                                    discoverFragment.getActivity()));

                            mDiscover.setProfileOwner(mDiscoverProfile);
                            //mDiscoverFriend.setProfileDiscover();
                            //TODO: Anadir lista de sugerencias

                            discoverFragment.addDiscoverData(mDiscoverProfile);

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

        // Adding request to request queue
        Controller.getInstance(discoverFragment.getActivity()).addToRequestQueue(jsonObjReq,
                tag_json_obj);
    }
    public void getDiscoverMedia(final DiscoverFragment discoverFragment, String accesstoken, String clientid, final DiscoverAdapter adapter) {

        pDialog = new ProgressDialog(discoverFragment.getActivity());
        pDialog.setMessage("Loading...");
        pDialog.show();

        final ArrayList<ImageItem> discoverMedia = new ArrayList<>();

        // https://api.instagram.com/v1/users/{user-id}/media/recent/?access_token=ACCESS-TOKEN
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,Globals.API_URL + "/media/popular"
                + "/?access_token=" + accesstoken, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            JSONArray array = response.getJSONArray("data");
                            //JSONObject pagination = response.getJSONObject("pagination");

                            for(int i=0; i<array.length(); i++)
                            {
                                String uriImage = array.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution").getString("url");
                                Log.i(TAG, uriImage);

                                ImageView imageView = new ImageView(discoverFragment.getActivity());
                                ImageRequest.makeImageRequest(uriImage, discoverFragment.getActivity(), imageView, adapter);
                                discoverMedia.add(new ImageItem(imageView));

                                //usermedia.add(new ImageItem(uriImage));
                                //usermedia.add(new ImageItem(ImageRequest.makeImageRequest(uriImage, profileFragment.getActivity(), adapter)));
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

    private Bitmap makeImageRequest(String urlImage, Context context) {


        ImageLoader imageLoader = Controller.getInstance(context).getImageLoader();
        final ImageView imageView = new ImageView(context);
        Bitmap discoverPic = null;

        // If you are using normal ImageView
        imageLoader.get(urlImage, new ImageLoader.ImageListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageView
                    //imageView.setImageBitmap(response.getBitmap();

                }
            }
        });

        // Loading image with placeholder and error image
//        imageLoader.get(urlImage, ImageLoader.getImageListener(
//                imageView, R.drawable.ico_loading, R.drawable.ico_error));

        Cache cache = Controller.getInstance(context).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlImage);
        if(entry != null){
            try {
                //String data = new String(entry.data, "UTF-8");
                String data = Base64.encodeToString(entry.data, Base64.DEFAULT);

                // handle data, like converting it to xml, json, bitmap etc.,

                //convert the base64 enconde string into a bitmap
                byte[] bytes = Base64.decode(data, Base64.DEFAULT);
                discoverPic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            // cached response doesn't exists. Make a network call here
        }


        return discoverPic;
    }


}
