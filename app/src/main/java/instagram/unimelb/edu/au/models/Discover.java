package instagram.unimelb.edu.au.models;

import java.util.List;

/**
 * Create an object Discover for the
 * Class Discover.
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
