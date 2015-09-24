package instagram.unimelb.edu.au.models;

import android.widget.ImageView;

/**
 * Created by Angela on 9/21/2015.
 */
public class FollowerActivityFeed {
    private ImageView profilepic;
    private String username;
    private String fullname;

    public FollowerActivityFeed(ImageView profilepic, String username, String fullname) {
        this.profilepic = profilepic;
        this.username = username;
        this.fullname = fullname;
    }

    public ImageView getProfilepic() {
        return profilepic;
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
