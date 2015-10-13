package instagram.unimelb.edu.au.models;

import android.graphics.Bitmap;

/**
 * Models the Profile
 */
public class Profile {

    private String id;
    private String username;
    private String fullname;
    private Bitmap profilepic;
    private String profilepic_url;
    private Integer numberposts;
    private Integer numberfollowers;
    private Integer numberfollowing;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getProfilepic_url() {
        return profilepic_url;
    }

    public void setProfilepic_url(String profilepic_url) {
        this.profilepic_url = profilepic_url;
    }


    public Integer getNumberposts() {
        return numberposts;
    }

    public void setNumberposts(Integer numberposts) {
        this.numberposts = numberposts;
    }

    public Integer getNumberfollowers() {
        return numberfollowers;
    }

    public void setNumberfollowers(Integer numberfollowers) {
        this.numberfollowers = numberfollowers;
    }

    public Integer getNumberfollowing() {
        return numberfollowing;
    }

    public void setNumberfollowing(Integer numberfollowing) {
        this.numberfollowing = numberfollowing;
    }





}
