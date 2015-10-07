package instagram.unimelb.edu.au.utils;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import instagram.unimelb.edu.au.MainActivity;

/**
 * Created by pc on 9/12/2015.
 */
public class Globals {
    public static final String CLIENT_ID = "3bffcf826cf8466da6c0042de97281be";
    public static final String CLIENT_SECRET = "65e961dd08e34810872b14e63654da80";
    public static final String CALLBACK_URL = "instagram://connect";

    public static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    public static final String TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    public static final String API_URL = "https://api.instagram.com/v1";

    public static String PROFILE_MEDIA_MAX_ID = "";
    public static String YOUACTIVITY_MEDIA_MAX_ID = "";
    public static int FOLLOWEDBY_MEDIA_MAX_ID = 0;
    public static String FOLLOWERACTIVITY_MEDIA_MAX_ID = "";
    public static String FRIENDS_MEDIA_MAX_ID = "";
    public static String SUGGESTEDFRIENDS_MEDIA_MAX_ID = "";
    public static String USERFEED_MAX_ID = "";
    public static String USERNAME = "";
    public static String FULL_NAME = "";
    public static String PROFILE_PIC_URL = "";
    public static int GALLERY_MEDIA_MAX_ID = 0;
    public static String GALLERY_SELECTEDPATH = "";
    public static MainActivity mainActivity=null;

    public static Boolean switchState = false; //to save the state of the userfeed sort by switch

    //public static int numberLoads = 0; // to know when the 5 initial image loading fragment has completed their loads.


    //Used for Bluetooth communication
    public static ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();
    public static BluetoothAdapter mBluetoothAdapter = null;
    public static ArrayAdapter<String> mPairedDevicesArrAdapter=null;
    public static ProgressDialog progressDialog =null;







}
