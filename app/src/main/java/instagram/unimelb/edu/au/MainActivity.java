package instagram.unimelb.edu.au;


//import android.support.v7.app.ActionBar;
//import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import instagram.unimelb.edu.au.adapters.MainFragmentPagerAdapter;
import instagram.unimelb.edu.au.networking.Connection;
import instagram.unimelb.edu.au.utils.Globals;

//import android.app.Activity;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Globals.mainActivity = this;

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setLogo(R.drawable.instagram_text_logo);
        getSupportActionBar().setTitle(null);

        Intent intent = getIntent();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        //CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.viewpager); //use this custom viewpager to avoid the user to sliding through tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        MainFragmentPagerAdapter pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this, intent);
        viewPager.setAdapter(pagerAdapter);


        // Give the TabLayout the ViewPager

        //viewPager.setPagingEnabled(false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //Change the toolbar according to the Fragment

                if (position == 0) {
                    getSupportActionBar().setTitle(null);
                    getSupportActionBar().setLogo(R.drawable.instagram_text_logo);
                } else if (position == 1) {
                    getSupportActionBar().setTitle("Search");
                    getSupportActionBar().setLogo(R.drawable.abc_ic_search_api_mtrl_alpha);
                } else if (position == 2) {
                    getSupportActionBar().setTitle(null);
                    getSupportActionBar().setLogo(null);
                } else if (position == 3) {
                    getSupportActionBar().setTitle(R.string.activityfeed_title);
                    getSupportActionBar().setLogo(null);
                } else {
                    getSupportActionBar().setTitle(Html.fromHtml("<b>" + Globals.USERNAME.toUpperCase() + "</b>"));
                    getSupportActionBar().setLogo(null);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        TextView tab_home = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tab_home.setBackgroundResource(R.drawable.btn_userfeed_state);
        tabLayout.getTabAt(0).setCustomView(tab_home);

        TextView tab_search = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tab_search.setBackgroundResource(R.drawable.btn_discover_state);
        tabLayout.getTabAt(1).setCustomView(tab_search);

        TextView tab_photo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tab_photo.setBackgroundResource(R.drawable.btn_photo);
        tabLayout.getTabAt(2).setCustomView(tab_photo);

        TextView tab_activity = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tab_activity.setBackgroundResource(R.drawable.btn_activityfeed_state);
        tabLayout.getTabAt(3).setCustomView(tab_activity);

        TextView tab_profile = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab,null);
        tab_profile.setBackgroundResource(R.drawable.btn_profile_state);
        tabLayout.getTabAt(4).setCustomView(tab_profile);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logOut();
            //return true;
        }

        if (id == android.R.id.home) { //This can be pressed when user is Comments or Likes fragment
            onBackPressed();
            //Show to tabs,title and logo that are by default in UserFeed
            tabLayout.setVisibility(View.VISIBLE);
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setLogo(R.drawable.instagram_text_logo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); //hide the back button
        }


        return super.onOptionsItemSelected(item);
    }

    public void logOut() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                    MainActivity.this);
            builder.setMessage("Log out from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Connection.resetAccessToken();
                                    MainActivity.this.finish();

                                    //Reset the globals MAX_ID's
                                    Globals.USERFEED_MAX_ID="";
                                    Globals.PROFILE_MEDIA_MAX_ID="";
                                    Globals.YOUACTIVITY_MEDIA_MAX_ID="";

                                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
        alert.show();

    }

    //To avoid to the user's log in when exit from main activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }
}
