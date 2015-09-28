package instagram.unimelb.edu.au.models;

import android.widget.ImageView;

import java.util.Date;

/**
 * Created by Angela on 9/17/2015.
 */
public class YouActivityFeed {

    public enum typeActivity { LIKE
    { public String toString() {
        return "liked your photo.";
    }},
        COMMENT
     { public String toString() {
         return "left a comment on your photo: ";
     }}};
    private String username;
    private ImageView urlProfilePic;
    private ImageView urlPhoto;
    private typeActivity type;
    private Date datePublished;
    private String comment;
    private String timePublication;



    public YouActivityFeed(String username, ImageView urlProfilePic, ImageView urlPhoto, typeActivity type, Date datePublished, String comment, String timePublication) {
        this.username = username;
        this.urlProfilePic = urlProfilePic;
        this.urlPhoto = urlPhoto;
        this.type = type;
        this.datePublished = datePublished;
        this.comment = comment;
        this.timePublication = timePublication;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ImageView getUrlProfilePic() {
        return urlProfilePic;
    }

    public void setUrlProfilePic(ImageView urlProfilePic) {
        this.urlProfilePic = urlProfilePic;
    }

    public ImageView getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(ImageView urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getType() {
        return type.toString();
    }

    public void setType(typeActivity type) {
        this.type = type;
    }

    public Date getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(Date datePublished) {
        this.datePublished = datePublished;
    }

    public String getTimePublication() {
        return timePublication;
    }

    public void setTimePublication(String timePublication) {
        this.timePublication = timePublication;
    }


}
