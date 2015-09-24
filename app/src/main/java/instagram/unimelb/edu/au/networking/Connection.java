package instagram.unimelb.edu.au.networking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import instagram.unimelb.edu.au.LoginActivity.OAuthDialogListener;
import instagram.unimelb.edu.au.utils.Globals;
import instagram.unimelb.edu.au.utils.Stream;

/**
 * Created by pc on 9/12/2015.
 */
public class Connection{

    private static Session mSession;
    private OAuthAuthenticationListener mListener;
    private ProgressDialog mProgress;
    private HashMap<String, String> userInfo = new HashMap<String, String>();
    private String mAuthUrl;
    private String mTokenUrl;
    private static String mAccessToken;
    private Context mCtx;

    private String mClientId;
    private String mClientSecret;

    public static int WHAT_FINALIZE = 0;
    static int WHAT_ERROR = 1;
    private static int WHAT_FETCH_INFO = 2;

    private OAuthDialogListener dialoglistener;

    /**
     * Callback url, as set in 'Manage OAuth Costumers' page
     * (https://developer.github.com/)
     */

    public static String mCallbackUrl = "";


    private static final String TAG = "InstagramAPI";

    public static final String TAG_DATA = "data";
    public static final String TAG_ID = "id";
    public static final String TAG_PROFILE_PICTURE = "profile_picture";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_BIO = "bio";
    public static final String TAG_WEBSITE = "website";
    public static final String TAG_COUNTS = "counts";
    public static final String TAG_FOLLOWS = "follows";
    public static final String TAG_FOLLOWED_BY = "followed_by";
    public static final String TAG_MEDIA = "media";
    public static final String TAG_FULL_NAME = "full_name";
    public static final String TAG_META = "meta";
    public static final String TAG_CODE = "code";

    public Connection(Context context, String clientId, String clientSecret,
                        String callbackUrl) {

        mClientId = clientId;
        mClientSecret = clientSecret;
        mCtx = context;
        mSession = new Session(context);
        mAccessToken = mSession.getAccessToken();
        mCallbackUrl = callbackUrl;
        mTokenUrl = Globals.TOKEN_URL + "?client_id=" + clientId + "&client_secret="
                + clientSecret + "&redirect_uri=" + mCallbackUrl
                + "&grant_type=authorization_code";
        mAuthUrl = Globals.AUTH_URL
                + "?client_id="
                + clientId
                + "&redirect_uri="
                + mCallbackUrl
                + "&response_type=code&display=touch&scope=likes+comments+relationships";

        dialoglistener = new OAuthDialogListener() {
            @Override
            public void onComplete(String code) {
                getAccessToken(code);
            }

            @Override
            public void onError(String error) {
                mListener.onFail("Authorization failed");
            }
        };

        //mDialog = new InstagramDialog(context, mAuthUrl, listener);
        mProgress = new ProgressDialog(context);
        mProgress.setCancelable(false);
    }

    private void getAccessToken(final String code) {
        mProgress.setMessage("Getting access token ...");
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Getting access token");
                int what = WHAT_FETCH_INFO;
                try {
                    URL url = new URL(Globals.TOKEN_URL);
                    // URL url = new URL(mTokenUrl + "&code=" + code);
                    Log.i(TAG, "Opening Token URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);
                    // urlConnection.connect();
                    OutputStreamWriter writer = new OutputStreamWriter(
                            urlConnection.getOutputStream());
                    writer.write("client_id=" + mClientId + "&client_secret="
                            + mClientSecret + "&grant_type=authorization_code"
                            + "&redirect_uri=" + mCallbackUrl + "&code=" + code);
                    writer.flush();
                    String response = Stream.streamToString(urlConnection
                            .getInputStream());
                    Log.i(TAG, "response " + response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();

                    mAccessToken = jsonObj.getString("access_token");
                    Log.i(TAG, "Got access token: " + mAccessToken);

                    String id = jsonObj.getJSONObject("user").getString("id");
                    String user = jsonObj.getJSONObject("user").getString(
                            "username");

                    Globals.USERNAME = user;

                    String name = jsonObj.getJSONObject("user").getString(
                            "full_name");

                    mSession.storeAccessToken(mAccessToken, id, user, name);

                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }

                mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
            }
        }.start();
    }




    public void fetchUserName(final Handler handler) {

        mProgress = new ProgressDialog(mCtx);
        mProgress.setMessage("Loading ...");
        mProgress.show();

        new Thread() {
            @Override
            public void run() {
                Log.i(TAG, "Fetching user info");
                int what = WHAT_FINALIZE;
                try {
                    URL url = new URL(Globals.API_URL + "/users/" + mSession.getId()
                            + "/?access_token=" + mAccessToken);

                    Log.d(TAG, "Opening URL " + url.toString());
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoInput(true);
                    urlConnection.connect();
                    String response = Stream.streamToString(urlConnection
                            .getInputStream());
                    System.out.println(response);
                    JSONObject jsonObj = (JSONObject) new JSONTokener(response)
                            .nextValue();


                    JSONObject data_obj = jsonObj.getJSONObject(TAG_DATA);
                    userInfo.put(TAG_ID, data_obj.getString(TAG_ID));

                    userInfo.put(TAG_PROFILE_PICTURE,
                            data_obj.getString(TAG_PROFILE_PICTURE));

                    Globals.PROFILE_PIC_URL = data_obj.getString(TAG_PROFILE_PICTURE);

                    userInfo.put(TAG_USERNAME, data_obj.getString(TAG_USERNAME));


                    userInfo.put(TAG_BIO, data_obj.getString(TAG_BIO));

                    userInfo.put(TAG_WEBSITE, data_obj.getString(TAG_WEBSITE));

                    JSONObject counts_obj = data_obj.getJSONObject(TAG_COUNTS);

                    userInfo.put(TAG_FOLLOWS, counts_obj.getString(TAG_FOLLOWS));

                    userInfo.put(TAG_FOLLOWED_BY,
                            counts_obj.getString(TAG_FOLLOWED_BY));

                    userInfo.put(TAG_MEDIA, counts_obj.getString(TAG_MEDIA));


                    userInfo.put(TAG_FULL_NAME,
                            data_obj.getString(TAG_FULL_NAME));

                    JSONObject meta_obj = jsonObj.getJSONObject(TAG_META);

                    userInfo.put(TAG_CODE, meta_obj.getString(TAG_CODE));
                } catch (Exception ex) {
                    what = WHAT_ERROR;
                    ex.printStackTrace();
                }
                mProgress.dismiss();
                handler.sendMessage(handler.obtainMessage(what, 2, 0));
            }
        }.start();

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_ERROR) {
                mProgress.dismiss();
                if (msg.arg1 == 1) {
                    mListener.onFail("Failed to get access token");
                } else if (msg.arg1 == 2) {
                    mListener.onFail("Failed to get user information");
                }
            } else if (msg.what == WHAT_FETCH_INFO) {
                // fetchUserName();
                mProgress.dismiss();
                mListener.onSuccess();
            }
        }
    };

    public HashMap<String, String> getUserInfo() {
        return userInfo;
    }

    public boolean hasAccessToken() {
        return (mAccessToken == null) ? false : true;
    }

    public void setListener(OAuthAuthenticationListener listener) {
        mListener = listener;
    }

    public String getUserName() {
        return mSession.getUsername();
    }

    public String getId() {
        return mSession.getId();
    }

    public String getName() {
        return mSession.getName();
    }
    public String getTOken() {
        return mSession.getAccessToken();
    }
    public void authorize() {
        // Intent webAuthIntent = new Intent(Intent.ACTION_VIEW);
        // webAuthIntent.setData(Uri.parse(AUTH_URL));
        // mCtx.startActivity(webAuthIntent);
        //mDialog.show();



    }

    public String getmAuthUrl(){
        return  mAuthUrl;
    }

    public OAuthDialogListener getdialoglistener(){
        return dialoglistener;
    }

    public static void resetAccessToken() {
        if (mAccessToken != null) {
            mSession.resetAccessToken();
            mAccessToken = null;
        }
    }


    public interface OAuthAuthenticationListener {
        public abstract void onSuccess();

        public abstract void onFail(String error);
    }


}
