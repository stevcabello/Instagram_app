package instagram.unimelb.edu.au.adapters;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import instagram.unimelb.edu.au.fragments.FollowingActivityFeedFragment;
import instagram.unimelb.edu.au.fragments.YouActivityFeedFragment;

public class ActivityFeedViewPagerAdapter extends FragmentPagerAdapter {

    int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "FOLLOWING", "YOU"};
    private Context context;
    private String accessToken;
    private String clientId;
    //test comment

    public ActivityFeedViewPagerAdapter(FragmentManager fm, Context context, String accessToken, String clientId) {
        super(fm);
        this.context = context;
        this.accessToken = accessToken;
        this.clientId = clientId;
    }

    @Override
    public Fragment getItem(int position) {
       // return fragments.get(position);
        if (position==0)
            return FollowingActivityFeedFragment.newInstance(accessToken, clientId);
        else
            return YouActivityFeedFragment.newInstance(accessToken, clientId);

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
