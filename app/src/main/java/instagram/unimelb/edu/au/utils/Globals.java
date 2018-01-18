package instagram.unimelb.edu.au.utils;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import instagram.unimelb.edu.au.MainActivity;
import instagram.unimelb.edu.au.models.Profile;

/**
 * Global variables
 */
public class Globals {

    //This variables are used in the authorization request
    public static final String CLIENT_ID = "2e9740b925d740c18c1866ed2172b86e";
    public static final String CLIENT_SECRET = "a339407a2a0e4cee85ca7858093f65ee";
    public static final String CALLBACK_URL = "instagram://connect";

    //URL for the authorization request
    public static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";

    //URL for the access_token request
    public static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";

    //URL used in all the other request the app does to Instagram (e.g. userfeed data, discover data).
    public static final String API_URL = "https://api.instagram.com/v1";

    //This variables are used in the pagination of some fragments
    public static String PROFILE_MEDIA_MAX_ID = "";
    public static String YOUACTIVITY_MEDIA_MAX_ID = "";
    public static int FOLLOWEDBY_MEDIA_MAX_ID = 0;
    public static String FOLLOWERACTIVITY_MEDIA_MAX_ID = "";
    public static String FRIENDS_MEDIA_MAX_ID = "";
    public static String SUGGESTEDFRIENDS_MEDIA_MAX_ID = "";
    public static String USERFEED_MAX_ID = "";
    public static Long GALLERY_MEDIA_MAX_ID = 99999999L;
    public static String GALLERY_SELECTEDPATH = "";


    public static MainActivity mainActivity=null; //just easy access to MainActivity methods

    public static Boolean switchState = false; //to save the state of the userfeed sort by switch


    //Used for Bluetooth communication
    public static ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
    public static BluetoothAdapter mBluetoothAdapter = null;
    public static ArrayAdapter<String> mPairedDevicesArrAdapter=null;
    public static ProgressDialog progressDialog =null;
    public static ArrayList<String> likesMedia = new ArrayList<>();


    //The user profile will be load with data on the initial connection
    public static Profile profile= new Profile();


    //Clear all the variables
    public static void resetGlobals() {
        PROFILE_MEDIA_MAX_ID = "";
        YOUACTIVITY_MEDIA_MAX_ID = "";
        FOLLOWEDBY_MEDIA_MAX_ID = 0;
        FOLLOWERACTIVITY_MEDIA_MAX_ID = "";
        FRIENDS_MEDIA_MAX_ID = "";
        SUGGESTEDFRIENDS_MEDIA_MAX_ID = "";
        USERFEED_MAX_ID = "";
        GALLERY_MEDIA_MAX_ID = 99999999L;
        GALLERY_SELECTEDPATH = "";
        mainActivity=null;
        switchState = false;
        bluetoothDevices = new ArrayList<BluetoothDevice>();
        mBluetoothAdapter = null;
        mPairedDevicesArrAdapter=null;
        progressDialog =null;
        likesMedia = new ArrayList<>();
        profile= new Profile();
    }

}
