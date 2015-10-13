package instagram.unimelb.edu.au.models;

import android.widget.ImageView;

/**
 *Class for Searching Profles
 */
public class Search {
    private String username;
    private String full_name;
    private ImageView profilepic;


    public Search(String username, String full_name, ImageView profilepic) {
        this.profilepic = profilepic;
        this.full_name = full_name;
        this.username = username;
    }


    public ImageView getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(ImageView profilepic) {
        this.profilepic = profilepic;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }






}
