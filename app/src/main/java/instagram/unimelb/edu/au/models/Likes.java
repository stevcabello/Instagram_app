package instagram.unimelb.edu.au.models;

import android.widget.ImageView;

/**
 * Models the Likes
 */
public class Likes {
    private String username;
    private String full_name;
    private ImageView profilepic;
    private String profilepic_url;


    public Likes(String username,String full_name,ImageView profilepic, String profilepic_url) {
        this.profilepic = profilepic;
        this.full_name = full_name;
        this.username = username;
        this.profilepic_url = profilepic_url;
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


    public String getProfilepic_url() {
        return profilepic_url;
    }

    public void setProfilepic_url(String profilepic_url) {
        this.profilepic_url = profilepic_url;
    }





}
