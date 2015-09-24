package instagram.unimelb.edu.au.utils;

import android.graphics.Bitmap;
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

}
