package instagram.unimelb.edu.au.models;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Carina on 28/09/15.
 */
public class SuggestedFriends {
    private ImageView profilepic;
    private String urlprofilepic;
    private String username;
    private String fullname;
    private String id;
    private ArrayList<ImageItem> imageItems;

    public SuggestedFriends(String username, String fullname, String id, ImageView profilepic) {
        this.username = username;
        this.fullname = fullname;
        this.id = id;
        this.profilepic = profilepic;
    }


    public SuggestedFriends(String id) {
        this.id = id;
    }

    public ImageView getProfilepic() {
        return profilepic;
    }

    public String getUrlprofilepic() {
        return urlprofilepic;
    }

    public String getUsername() {
        return username;
    }

    public String getFullname() {
        return fullname;
    }

    public String getId() {
        return id;
    }

    public ArrayList<ImageItem> getImageItems() {
        return imageItems;
    }

    public void setProfilepic(ImageView profilepic) {
        this.profilepic = profilepic;
    }

    public void setUrlprofilepic(String urlprofilepic) {
        this.urlprofilepic = urlprofilepic;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageItems(ArrayList<ImageItem> imageItems) {
        this.imageItems = imageItems;
    }
}
