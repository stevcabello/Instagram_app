package instagram.unimelb.edu.au.models;

import java.util.List;

/**
 * Created by Carina on 19/09/15.
 */
public class Discover {

    private Profile profileOwner;
    private List<Profile> discoverFriends;

    public List<Profile> getProfileDiscover() {
        return discoverFriends;
    }

    public void setProfileDiscover(List<Profile> discoverFriends) {
        this.discoverFriends = discoverFriends;
    }

    public void setProfileOwner(Profile profileOwner) {
        this.profileOwner = profileOwner;
    }

    public Profile getProfileOwner() {
        return profileOwner;
    }
}
