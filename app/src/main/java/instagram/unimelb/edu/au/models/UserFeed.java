package instagram.unimelb.edu.au.models;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Models user feed data
 */
public class UserFeed {

    private String media_id;
    private String photo_url;
    private String profilepic_url;
    private ImageView profilepic;
    private String username;
    private ImageView photo;
    private String tag=""; //for the "In Range" tag
    private String type; //image or video
    private String created_time;
    private Integer numlikes;
    private String description;
    private Integer numcomments;
    private Double latitude =0.0;
    private Double longitude=0.0;
    private String location="";
    private float distanceToAuthUser;
    private ArrayList<Comments> comments;
    private ArrayList<Likes> likes;



    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }


    public String getProfilepic_url() {
        return profilepic_url;
    }

    public void setProfilepic_url(String profilepic_url) {
        this.profilepic_url = profilepic_url;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public Integer getNumcomments() {
        return numcomments;
    }

    public void setNumcomments(Integer numcomments) {
        this.numcomments = numcomments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public ImageView getPhoto() {
        return photo;
    }

    public void setPhoto(ImageView photo) {
        this.photo = photo;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public Integer getNumLikes() {
        return numlikes;
    }

    public void setNumLikes(Integer numlikes) {
        this.numlikes = numlikes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
    }

    public ArrayList<Likes> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Likes> likes) {
        this.likes = likes;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getDistanceToAuthUser() {
        return distanceToAuthUser;
    }

    public void setDistanceToAuthUser(float distanceToAuthUser) {
        this.distanceToAuthUser = distanceToAuthUser;
    }


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }




}
