package instagram.unimelb.edu.au.models;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Angela on 9/21/2015.
 */
public class FollowingActivityFeed {
    private ImageView profilepic;
    private String urlprofilepic;
    private String username;
    private String fullname;
    private String id;
    private ArrayList<ImageItem> imageItems;

    public FollowingActivityFeed(String username, String fullname, String id, String urlprofilepic) {
        this.username = username;
        this.fullname = fullname;
        this.id = id;
        this.urlprofilepic = urlprofilepic;
    }

    public String getUrlprofilepic() {
        return urlprofilepic;
    }

    public void setUrlprofilepic(String urlprofilepic) {
        this.urlprofilepic = urlprofilepic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ImageView getProfilepic() {
        return profilepic;
    }

    public ArrayList<ImageItem> getImageItems() {
        return imageItems;
    }

    public void setImageItems(ArrayList<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    public void setProfilepic(ImageView profilepic) {
        this.profilepic = profilepic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
