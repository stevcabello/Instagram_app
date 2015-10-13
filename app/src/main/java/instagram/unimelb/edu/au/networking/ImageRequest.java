package instagram.unimelb.edu.au.networking;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import instagram.unimelb.edu.au.R;


/**
 * Manages the load of images into the gridviews or listviews
 */
public class ImageRequest {

    private static String TAG = ImageRequest.class.getSimpleName();


    /**
     * Manages the images load from image's url into a ImageView in order to be displayed into
     * a gridview or listview, the adapter is passed as fourth argument to refresh the content
     * of the listview or gridview.
     *
     * @param urlImage
     * @param context
     * @param imageView
     * @param adapter
     */
    public static void makeImageRequest(String urlImage, Context context, final ImageView imageView, final ArrayAdapter adapter) {


        ImageLoader imageLoader = Controller.getInstance(context).getImageLoader();

        imageLoader.get(urlImage, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageView
                    imageView.setImageBitmap(response.getBitmap());
                    adapter.notifyDataSetChanged(); //Important!! refresh the gridview or listview

                }
            }
        });

        // Loading image with placeholder and error image
        imageLoader.get(urlImage, ImageLoader.getImageListener(
                imageView, R.drawable.watch_icon, R.drawable.tab_retry));

        Cache cache = Controller.getInstance(context).getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlImage);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            // cached response doesn't exists. Make a network call here
        }


    }






}
