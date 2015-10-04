package instagram.unimelb.edu.au.models;

import android.widget.ImageView;

/**
 * Models the comments
 */
public class Comments {
    private String username;
    private String text;
    private String created_time;
    private ImageView profilepic;
    private String profilepic_url;



    public Comments(String username, String text, String created_time, ImageView profilepic, String profilepic_url) {
        this.username = username;
        this.text = text;
        this.created_time = created_time;
        this.profilepic = profilepic;
        this.profilepic_url = profilepic_url;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ImageView getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(ImageView profilepic) {
        this.profilepic = profilepic;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getProfilepic_url() {
        return profilepic_url;
    }

    public void setProfilepic_url(String profilepic_url) {
        this.profilepic_url = profilepic_url;
    }


}
