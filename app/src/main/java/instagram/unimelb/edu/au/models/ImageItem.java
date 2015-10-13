package instagram.unimelb.edu.au.models;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Models ImageItem
 */
public class ImageItem {
    private Bitmap image;
    private String imageurl;
    private ImageView imageview;
    private String title;


    public ImageView getImageview() {
        return imageview;
    }

    public void setImageview(ImageView imageview) {
        this.imageview = imageview;
    }


    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


    public ImageItem(Bitmap image) {
        super();
        this.image = image;

    }

    public ImageItem(String imageurl) {
        super();
        this.imageurl = imageurl;

    }

    public ImageItem(ImageView imageview){
        super();
        this.imageview = imageview;
    }
    public ImageItem(ImageView imageview, String imageurl){
        super();
        this.imageview = imageview;
        this.imageurl = imageurl;
    }



}