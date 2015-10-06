package instagram.unimelb.edu.au.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

//TODO: Remember to add any lonely method and useful to all other classes to this class
/**
 * Manages images,dates
 */
public class Utils {


    /**
     * Converts an Imageview view passed as @param into a bitmap
     * @param imageView
     * @return
     */
    public static Bitmap getBitmap (ImageView imageView) {

        imageView.setDrawingCacheEnabled(true);

        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());

        imageView.buildDrawingCache(true);
        //TODO: in the line below I get a nullpointerexception
        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
        imageView.setDrawingCacheEnabled(false); // clear drawing cache

        return bitmap;
    }

    /**
     * Gets the elapsed time since the photo was post.
     * @param unixtime
     * @return
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
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
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




}
