package instagram.unimelb.edu.au.networking;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Session class to store important data of the authenticated user
 */
public class Session {


    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private static final String SHARED = "Instagram_Preferences";
    private static final String API_USERNAME = "username";
    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_ACCESS_TOKEN = "access_token";

    public Session(Context context) {
        sharedPref = context.getSharedPreferences(SHARED, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }


    /**
     * Stores access data
     * @param accessToken access_token for the application
     * @param id id of the authenticated usre
     * @param username username of the authenticated user
     * @param name fullname of the authenticated user
     */
    public void storeAccessData(String accessToken, String id, String username, String name) {
        editor.putString(API_ID, id);
        editor.putString(API_NAME, name);
        editor.putString(API_ACCESS_TOKEN, accessToken);
        editor.putString(API_USERNAME, username);
        editor.commit();
    }


    /**
     * Reset access token and user name
     */
    public void resetAccessToken() {
        editor.putString(API_ID, null);
        editor.putString(API_NAME, null);
        editor.putString(API_ACCESS_TOKEN, null);
        editor.putString(API_USERNAME, null);
        editor.commit();
    }

    /**
     * Get user name
     * @return User name
     */
    public String getUsername() {
        return sharedPref.getString(API_USERNAME, null);
    }

    /**
     * Get user id
     * @return user id
     */
    public String getId() {
        return sharedPref.getString(API_ID, null);
    }

    /**
     * Get user full name
     * @return user full name
     */
    public String getName() {
        return sharedPref.getString(API_NAME, null);
    }

    /**
     * Get access token
     * @return access token
     */
    public String getAccessToken() {
        return sharedPref.getString(API_ACCESS_TOKEN, null);
    }

}
