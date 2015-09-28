package instagram.unimelb.edu.au;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;

import instagram.unimelb.edu.au.networking.Connection;
import instagram.unimelb.edu.au.utils.Globals;

public class LoginActivity extends AppCompatActivity {

    public Button btn_login;

    public AlertDialog.Builder logindialog = null;
    private ProgressDialog mSpinner;


    private Connection mConnection;
    private HashMap<String, String> userInfoHashmap = new HashMap<String, String>();
    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == Connection.WHAT_FINALIZE) {
                userInfoHashmap = mConnection.getUserInfo();
            } else if (msg.what == Connection.WHAT_FINALIZE) {
                Toast.makeText(LoginActivity.this, "Check your network.",
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mConnection = new Connection(this, Globals.CLIENT_ID,
                Globals.CLIENT_SECRET, Globals.CALLBACK_URL);
        mConnection.setListener(new Connection.OAuthAuthenticationListener() {

            @Override
            public void onSuccess() {

                mConnection.fetchUserName(handler);

                // Open the MainActivity and send the accesstoken and clientid
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("accesstoken", mConnection.getTOken());
                intent.putExtra("clientid",mConnection.getId());

                LoginActivity.this.finish();
                startActivity(intent);
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT)
                        .show();
            }
        });


        if (mConnection.hasAccessToken()) {
            mConnection.fetchUserName(handler);
        }


        btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectUser();
            }
        });

        mSpinner = new ProgressDialog(LoginActivity.this);
        mSpinner.setMessage("Loading...");

    }


    private void connectUser() {
            logindialog = new AlertDialog.Builder(LoginActivity.this);
            LayoutInflater inflater = LoginActivity.this.getLayoutInflater();
            View loginView = inflater.inflate(R.layout.custom_insta_login, null);
            WebView webView = (WebView)loginView.findViewById(R.id.wv_instagram_login);
            webView.setWebViewClient(new OAuthWebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setFocusable(true);
            webView.setFocusableInTouchMode(true);
            webView.loadUrl(mConnection.getmAuthUrl());

            //Opens the webView for the client implicit authentication
            logindialog.setView(loginView);
            logindialog.create().show();
            logindialog.setCancelable(true);
    }


    private class OAuthWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("Instagram", "Redirecting URL " + url);

            if (url.startsWith(mConnection.mCallbackUrl)) {
                String urls[] = url.split("=");
                mConnection.getdialoglistener().onComplete(urls[1]);
                //InstagramDialog.this.dismiss();
                return true;
            }
            return false;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.d("Error", "Page error: " + description);

            super.onReceivedError(view, errorCode, description, failingUrl);
            mConnection.getdialoglistener().onError(description);
            //InstagramDialog.this.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("Instagram", "Loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            mSpinner.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            mSpinner.dismiss();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OAuthDialogListener {
        public abstract void onComplete(String accessToken);
        public abstract void onError(String error);
    }
}
