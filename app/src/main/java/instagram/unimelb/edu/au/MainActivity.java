package instagram.unimelb.edu.au;


//import android.support.v7.app.ActionBar;
//import android.app.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import instagram.unimelb.edu.au.adapters.CustomViewPager;
import instagram.unimelb.edu.au.adapters.MainFragmentPagerAdapter;
import instagram.unimelb.edu.au.businessobject.boUserFeed;
import instagram.unimelb.edu.au.fragments.ActivityFeedFragment;
import instagram.unimelb.edu.au.fragments.DiscoverFragment;
import instagram.unimelb.edu.au.fragments.PhotoFragment;
import instagram.unimelb.edu.au.fragments.ProfileFragment;
import instagram.unimelb.edu.au.fragments.SearchFragment;
import instagram.unimelb.edu.au.fragments.UserFeedFragment;
import instagram.unimelb.edu.au.networking.Connection;
import instagram.unimelb.edu.au.utils.Globals;
import instagram.unimelb.edu.au.utils.Utils;

//import android.app.Activity;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    TabLayout tabLayout;
    CustomViewPager viewPager;
    Switch switchAB;
    Fragment visibleFragment;
    String accessToken;
    String clientId;
    String TAG = MainActivity.class.getSimpleName();
    ProgressDialog dialog;
    double userLatitude;
    double userLongitude;
    SwitchCompat sortBy = null;

    public static final int SORT_BY_DATETIME = 0;
    public static final int SORT_BY_LOCATION = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Globals.mainActivity = this;

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setLogo(R.drawable.instagram_text_logo);
        getSupportActionBar().setTitle(null);

        visibleFragment = new UserFeedFragment(); //the Default first visible Fragment

        Intent intent = getIntent();
        accessToken = getIntent().getExtras().getString("accesstoken");
        clientId = getIntent().getExtras().getString("clientid");

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (CustomViewPager) findViewById(R.id.viewpager); //use this custom viewpager to avoid the user to sliding through tabs
        //ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        MainFragmentPagerAdapter pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this, intent);
        viewPager.setAdapter(pagerAdapter);


        // Give the TabLayout the ViewPager

        viewPager.setPagingEnabled(false); //to avoid the swip gesture between tabs
        viewPager.setOffscreenPageLimit(20);
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
                    tabLayout.setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    visibleFragment = new UserFeedFragment();
                } else if (position == 1) {
                    getSupportActionBar().setTitle("Search");
                    getSupportActionBar().setLogo(null);
                    tabLayout.setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false); //hide the back button
                    visibleFragment = new DiscoverFragment();
                } else if (position == 2) {
                    getSupportActionBar().setTitle("GALLERY"); //set with Gallery because is the tab 0 inside photofragment
                    getSupportActionBar().setLogo(null);
                    tabLayout.setVisibility(View.GONE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); //show the back button
                    visibleFragment = new PhotoFragment();
                } else if (position == 3) {
                    getSupportActionBar().setTitle(R.string.activityfeed_title);
                    getSupportActionBar().setLogo(null);
                    tabLayout.setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false); //hide the back button
                    visibleFragment = new ActivityFeedFragment();
                } else {
                    getSupportActionBar().setTitle(Html.fromHtml("<b>" + Globals.USERNAME.toUpperCase() + "</b>"));
                    getSupportActionBar().setLogo(null);
                    tabLayout.setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false); //hide the back button
                    visibleFragment = new ProfileFragment();
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
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item_search = menu.findItem(R.id.action_search);
        MenuItem item_settings = menu.findItem(R.id.action_settings);
        MenuItem item_bar_search = menu.findItem(R.id.action_bar_search);
        MenuItem item_switch = menu.findItem(R.id.action_switch);

        HandleSwitchSortBy(item_switch); //To handle the Switch sort by

        Fragment whichFragment = getVisibleFragment();
        //The Fragments that use item from the menu main are:
        // -UserFeedFragment --> switch
        // -DiscoverFragment --> search button
        // -ProfileFragment --> settings button
        // -SearchFragment --> search bar

        if (whichFragment!= null) {
            String shareVisible = whichFragment.getClass().toString();

            if (shareVisible.equals(UserFeedFragment.class.toString())){
                item_search.setVisible(false);
                item_settings.setVisible(false);
                item_bar_search.setVisible(false);
            }else if (shareVisible.equals(DiscoverFragment.class.toString())){
                item_settings.setVisible(false);
                item_switch.setVisible(false);
                item_bar_search.setVisible(false);
            }else if (shareVisible.equals(PhotoFragment.class.toString()) || shareVisible.equals(ActivityFeedFragment.class.toString())){
                item_search.setVisible(false);
                item_settings.setVisible(false);
                item_switch.setVisible(false);
                item_bar_search.setVisible(false);
            } else if (shareVisible.equals(ProfileFragment.class.toString())){
                item_search.setVisible(false);
                item_switch.setVisible(false);
                item_bar_search.setVisible(false);
            } else { //SearchFragment
                item_search.setVisible(false);
                item_switch.setVisible(false);
                item_settings.setVisible(false);
            }

            Log.i(TAG,"In fragment: "  + shareVisible);
        } else { // case visiblefragment is null
            item_search.setVisible(false);
            item_settings.setVisible(false);
            item_switch.setVisible(false);
            item_bar_search.setVisible(false);
        }




        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Fragment whichFragment = getVisibleFragment();
        String shareVisible = whichFragment.getClass().toString();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logOut();
        }

        else if (id == android.R.id.home) { //To handle the behaviour of the back button depending on the visibleFragment
            String title = getSupportActionBar().getTitle().toString();
            if (title.equals("COMMENTS") || title.equals("LIKES") ) {
                onBackPressed();
                tabLayout.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle(null);
                getSupportActionBar().setLogo(R.drawable.instagram_text_logo);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false); //hide the back button
            }else if (shareVisible.equals(SearchFragment.class.toString())) {
                onBackPressed();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                setVisibleFragment(new DiscoverFragment());
            }else if (title.equals("FILTER")){ //this will be useful in case we work with the FilterFragment
                onBackPressed();
            }

            else
                viewPager.setCurrentItem(0);

        }

        else if (id == R.id.action_switch) {
           // The behaviour of the Switch is handled in OnCreateOptionsMenu
        }


        else if (id == R.id.action_search) {
            //Opens searchFragment
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            SearchFragment sf= SearchFragment.newInstance(accessToken, clientId);
            fragmentTransaction.replace(R.id.fly_discover_fragment, sf);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        else if (id == R.id.action_bar_search) {
            SearchView sv = new SearchView(getSupportActionBar().getThemedContext());
            sv.setQueryHint("Search for users");
            MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
            MenuItemCompat.setActionView(item, sv);
            sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String querySearch = query;
                    SearchFragment.searchFragment.search(querySearch);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                /*TODO: This option takes a lot of time in the app*/
                /*search(newText);*/
                    return  true;
                }
            });

        }

        Log.i(TAG, item.toString());

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

    /**
     * Handles the behaviour of the Switch sort by Date/Time (not checked) or Location (checked)
     * @param item_switch
     */
    public void HandleSwitchSortBy(MenuItem item_switch) {

        final boUserFeed objUserFeed = new boUserFeed();

        item_switch.setActionView(R.layout.actionbar_switchsortby);

        sortBy = (SwitchCompat)item_switch.getActionView().findViewById(R.id.switchSortBy);

        sortBy.setChecked(Globals.switchState);

        sortBy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    GPSLocation gpsLocation = new GPSLocation();
                    gpsLocation.execute();
                } else {
                    Toast.makeText(getApplication(), "Feeds based on Date/Time", Toast.LENGTH_SHORT).show();
                    UserFeedFragment.getData(SORT_BY_DATETIME, 0, 0);
                    Globals.switchState = false;

                }
            }
        });

    }




    public Fragment getVisibleFragment() {
        return visibleFragment;
    }

    public void setVisibleFragment(Fragment fragment) {
        this.visibleFragment = fragment;
    }

    //To avoid to the user's log out when exit from main activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.d(this.getClass().getName(), "back button pressed");
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * Handles the getting of gps coordinates from the mobile phone
     * both providers (network and GPS) make the requestlocation
     */
    public class GPSLocation extends AsyncTask<String, Integer, String> {

        ProgressDialog progDailog = null;

        public int time = 0;
        public boolean outOfTime = false;

        public double lati = 0;
        public double longi = 0;

        public LocationManager mLocationManager;
        public MyLocationListener mLocationListener;

        String provider1,provider2;

        @Override
        protected void onPreExecute() {
            mLocationListener = new MyLocationListener();
            mLocationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Utils.displayPromptForEnablingGPS(MainActivity.this);
                sortBy.setChecked(false); //the switch continue in its default state (Datetime)
                Globals.switchState = false;
                GPSLocation.this.cancel(true);
            }else{
                //Network provider criteria
                Criteria criteria1 = new Criteria();
                criteria1.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria1.setPowerRequirement(Criteria.POWER_LOW);
                provider1 = mLocationManager.getBestProvider(criteria1,true);

                //GPS provider criteria
                Criteria criteria2 = new Criteria();
                criteria2.setAccuracy(Criteria.ACCURACY_FINE);
                provider2 = mLocationManager.getBestProvider(criteria2,true);


                if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                            provider1, 0, 0,
                            mLocationListener);
                }

                mLocationManager.requestLocationUpdates(
                        provider2, 0, 0,
                        mLocationListener);
            }

            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    GPSLocation.this.cancel(true);
                }
            });
            progDailog.setMessage("Getting GPS location... ");
            progDailog.setIndeterminate(true);
            progDailog.setCancelable(true);
            progDailog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            Timer t =new Timer();

            TimerTask tk = new TimerTask() {

                @Override
                public void run() {
                    outOfTime = true;
                }
            };

            t.schedule(tk, 1700000); //to wait approximately 30 secs.

            while(!outOfTime){
                if (lati != 0 && longi != 0) {
                    t.cancel();
                    tk.cancel();
                    break;
                }
            }

            if (outOfTime) return "err";

            return "ok";
        }


        @Override
        protected void onCancelled(){
            System.out.println("Cancelled by user!");
            progDailog.dismiss();
            mLocationManager.removeUpdates(mLocationListener);
        }

        @Override
        protected void onPostExecute(String result) {
            progDailog.dismiss();
            mLocationManager.removeUpdates(mLocationListener);

            if (result.equals("ok")) {
                userLatitude = lati;
                userLongitude = longi;

                UserFeedFragment.getData(SORT_BY_LOCATION, userLatitude, userLongitude);
                Globals.switchState = true;
                Toast.makeText(getApplication(), "Feeds based on Location", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "GPS location: " + String.valueOf(userLatitude) + " "+ String.valueOf(userLongitude) , Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(MainActivity.this,
                        "Please be located in an area of greater coverage or try again",
                        Toast.LENGTH_LONG).show();
                sortBy.setChecked(false);

            }

        }


        public class MyLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(Location location) {
                try {
                    lati = location.getLatitude();
                    longi = location.getLongitude();
                } catch (Exception e) {
                    Log.i(TAG,e.getMessage());
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i(TAG, "OnProviderDisabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i(TAG, "onProviderEnabled");
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.i(TAG, "onStatusChanged");

            }

        }

    }



}
