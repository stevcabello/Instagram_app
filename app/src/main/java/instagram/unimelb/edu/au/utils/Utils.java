package instagram.unimelb.edu.au.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;


/**
 * Manages images,dates and some other useful shared methods
 */
public class Utils {


    /**
     * Converts an Imageview view passed as @param into a bitmap
     * @param imageView
     * @return
     */
    public static Bitmap getBitmap (ImageView imageView) {
        Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        return bitmap;
    }


    /**
     * Gets the elapsed time since the photo was post.
     * @param unixtime the timestamp in unix format (i.e. long type number, milliseconds since 1970)
     * @return the time elapsed
     */
    public static String getElapsedtime(String unixtime, String format_type){

        //default format_type "short"
        String time_legend_seconds= "s";
        String time_legend_minutes="m";
        String time_legend_hours="h";
        String time_legend_days="d";
        String time_legend_weeks="w";

        if (format_type=="long"){
            time_legend_seconds=" seconds ago";
            time_legend_minutes=" minutes ago";
            time_legend_hours=" hours ago";
            time_legend_days=" days ago";
            time_legend_weeks=" weeks ago";
        }

        Long created_time = Long.parseLong(unixtime);
        Long current_time = System.currentTimeMillis() / 1000L;

        Long difference = (current_time - created_time);
        if (difference <= 0) return "Now";

        if (difference>60) { //more than 60 seconds
            difference = difference / 60;
            if (difference > 60) { //more than 60 minutes
              difference = difference /60;
                if (difference > 24) { // more than 24 hours
                    difference = difference / 24;
                    if (difference > 7){ // more than 7 days
                        return (difference/7) + time_legend_weeks; // returns the elapsed number of weeks;
                    } else return difference + time_legend_days; //returns the elapsed number of days;
                }else return difference + time_legend_hours; //returns the elapsed number of hours;
            }else return difference + time_legend_minutes; //returns the elapsed number of minutes;
        }else return difference + time_legend_seconds; //returns the elapsed number of seconds;


    }


    /**
     * To compute the distance between the authenticated user and the following users that have post something recently
     * @param lat1 latitude of the authenticated user
     * @param lon1 longitude of the authenticted user
     * @param lat2 latitude of the following user's post
     * @param lon2 longitude of the following user's post
     * @return the distance in meters between two geo points
     */
    public static float DistanceBetween(double lat1, double lon1, double lat2, double lon2) {
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);

        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);

        float distanceInMeters = loc1.distanceTo(loc2);

        return distanceInMeters;
    }


    /**
     * Ask user to enable the GPS
     * @param activity
     */
    public static void displayPromptForEnablingGPS(
            final Activity activity)
    {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Enable GPS service to find current location. "
                + " Click OK to go to location services settings to let you do so.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }


    /**
     * Given an array with the media-id to whom the authenticated user gave likes, return
     * an array with some of those media-id in order to provide synthetic likes to the followers
     * of my followers.
     *
     * @param arrSyntheticLikes
     * @return array of synthetic likes (false likes)
     */
    public static ArrayList<String> getRandomIdList(ArrayList<String> arrSyntheticLikes){

        ArrayList<String> arrIds = new ArrayList<>();

        //Get the total size of media-id likes from the authenticated user
        int total = arrSyntheticLikes.size();
        Log.i("Utils random total",String.valueOf(total));

        //Get a random number between 0 and the length of the media-id arrays from the authenticated user
        Random r = new Random();
        int rndNumber = r.nextInt(total + 1) + 0;
        Log.i("Utils random",String.valueOf(rndNumber));

        //Set the random number as top for the loop in order to provide a random set of likes into
        // the array to return
        for(int i=0; i<rndNumber; i++){
            arrIds.add(arrSyntheticLikes.get(i));
        }

        return arrSyntheticLikes;

    }

}
