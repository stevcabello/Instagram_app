package instagram.unimelb.edu.au.networking;

import android.content.Context;

import instagram.unimelb.edu.au.LoginActivity.OAuthDialogListener;
import instagram.unimelb.edu.au.businessobject.boLogin;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Manages the initial connection to Instagram
 */
public class Connection{

    private static Session mSession;
    private OAuthAuthenticationListener mListener;
    private String mAuthUrl;
    private static String mAccessToken;
    private Context mCtx;
    private String mClientId;
    private String mClientSecret;
    private OAuthDialogListener dialoglistener;
    public static String mCallbackUrl = "";

    public boLogin  objLogin = new boLogin();

    public Connection(Context context, final String clientId, final String clientSecret,
                        final String callbackUrl) {

        mClientId = clientId;
        mClientSecret = clientSecret;
        mCtx = context;
        mSession = new Session(context);
        mAccessToken = mSession.getAccessToken();
        mCallbackUrl = callbackUrl;

        //To request the authorization code
        mAuthUrl = Globals.AUTH_URL
                + "?client_id="
                + clientId
                + "&redirect_uri="
                + mCallbackUrl
                + "&response_type=code&display=touch&scope=likes+comments+relationships";

        dialoglistener = new OAuthDialogListener() {
            @Override
            public void onComplete(String code) {
                //To request the accesstoken
                objLogin.getAccessToken(mCtx,code,mClientId,mClientSecret,mCallbackUrl,mSession,mListener);

            }

            @Override
            public void onError(String error) {
                mListener.onFail("Authorization failed");
            }
        };

    }


    /**
     * Get the userprofile's data
     */
    public void getUserProfileData() {
        //To request user profile data
        objLogin.getUserProfileData(mCtx,mSession,mListener);
    }


    /**
     * Checks for the state of the access_token for the application
     * @return true if token is still available, false otherwise
     */
    public boolean hasAccessToken() {
        return (mAccessToken == null) ? false : true;
    }

    /**
     * Lisnter for the authentication
     * @param listener
     */
    public void setListener(OAuthAuthenticationListener listener) {
        mListener = listener;
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
    public String getmAuthUrl(){
        return  mAuthUrl;
    }

    public OAuthDialogListener getdialoglistener(){
        return dialoglistener;
    }

    /**
     * To reset access token
     */
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
