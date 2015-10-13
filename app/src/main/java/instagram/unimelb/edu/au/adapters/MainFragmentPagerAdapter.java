package instagram.unimelb.edu.au.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import instagram.unimelb.edu.au.fragments.ActivityFeedFragment;
import instagram.unimelb.edu.au.fragments.DiscoverFragment;
import instagram.unimelb.edu.au.fragments.PhotoFragment;
import instagram.unimelb.edu.au.fragments.ProfileFragment;
import instagram.unimelb.edu.au.fragments.UserFeedFragment;

/**
 * Adapter to handle the actions when moving through "tabs" (i.e. Fragments).
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 5;
    private String tabTitles[] = new String[] { "UserFeed", "Discover", "Photo", "ActivityFeed","Profile" };
    private Context context;
    private String accesstoken;
    private String clientid;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context, Intent intent) {
        super(fm);
        this.context = context;
        this.accesstoken = intent.getExtras().getString("accesstoken"); //receive the access_token from MainActivity
        this.clientid = intent.getExtras().getString("clientid"); //receive the client_id form MainActivity
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) { //Depending on the selected fragment we create an instance of it with the access_token and client_id
        if (position==0)
             return UserFeedFragment.newInstance(accesstoken,clientid);
        else if (position==1)
             return DiscoverFragment.newInstance(accesstoken, clientid);
        else if (position==2)
            return PhotoFragment.newInstance(accesstoken, clientid);
        else if (position==3)
            return ActivityFeedFragment.newInstance(accesstoken,clientid);
        else
            return ProfileFragment.newInstance(accesstoken,clientid);

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
          return tabTitles[position];
    }




}
