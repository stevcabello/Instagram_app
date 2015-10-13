package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.ProfileAdapter;
import instagram.unimelb.edu.au.businessobject.boProfile;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.models.Profile;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Fragment for userProfile tab
 */
public class ProfileFragment extends Fragment {

    private static final String ARG_ACCESS_TOKEN = "access_token";
    private static final String ARG_CLIENT_ID = "client_id";

    private String mAccessToken;
    private String mClientId;

    private OnFragmentInteractionListener mListener;

    private String TAG = "ProfileFragment";

    private GridViewWithHeaderAndFooter gridView;

    private ProfileAdapter gridAdapter;

    public ProfileFragment profileFragment;

    private View rootView;

    ImageView profilepic;
    TextView numberposts;
    TextView numberfollowers;
    TextView numberfollowing;

    boProfile objprofile;

    Boolean userScrolled = false;

    Integer nposts;

    View header_profile;


    /**
     * Create a new instance of ProfileFragment
     * @param accesstoken access_token of the application
     * @param clientid id of the authenticated user
     * @return
     */
    public static ProfileFragment newInstance(String accesstoken, String clientid) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ACCESS_TOKEN, accesstoken);
        args.putString(ARG_CLIENT_ID, clientid);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mAccessToken = getArguments().getString(ARG_ACCESS_TOKEN);
            mClientId = getArguments().getString(ARG_CLIENT_ID);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //To avoid reloading the view everytime user access to profile
        if (rootView != null) {
            return rootView;
        }

        setHasOptionsMenu(true); //to enable the settings action button

        // Inflate the layout for this
        rootView = inflater.inflate(R.layout.fragment_profile,container,false);

        profileFragment = this;

        gridView = (GridViewWithHeaderAndFooter)rootView.findViewById(R.id.gv_userphotos);

        //add the header to the gridview
        header_profile = LayoutInflater.from(getActivity()).inflate(R.layout.header_profile,null,false);
        gridView.addHeaderView(header_profile,"",true);

        gridAdapter = new ProfileAdapter(this.getActivity(),R.layout.item_discover,new ArrayList<ImageItem>());
        gridView.setAdapter(gridAdapter);

        setProfileData(Globals.profile);
        objprofile = new boProfile();
        objprofile.getProfileMedia(profileFragment, mAccessToken, mClientId, gridAdapter);

        return rootView;

    }



    /**
     * Set the profile data of the authenticated user
     * @param userprofile profilefragment
     */
    public void setProfileData(Profile userprofile) {
        profilepic = (ImageView)header_profile.findViewById(R.id.iv_profilepic);
        numberposts = (TextView)header_profile.findViewById(R.id.tv_numberposts);
        numberfollowers = (TextView)header_profile.findViewById(R.id.tv_numberfollowers);
        numberfollowing = (TextView)header_profile.findViewById(R.id.tv_numberfollowing);

        String profile_pic_url = userprofile.getProfilepic_url();
        ImageRequest.makeImageRequest(profile_pic_url, getActivity(), profilepic, gridAdapter);

        numberposts.setText(String.valueOf(userprofile.getNumberposts()));
        nposts = userprofile.getNumberposts();
        numberfollowers.setText(String.valueOf(userprofile.getNumberfollowers()));
        numberfollowing.setText(String.valueOf(userprofile.getNumberfollowing()));
    }


    /**
     * Add new media data to the userprofile's gridview
     * @param usermedia Array of ImageItems
     */
    public void addProfileMedia(ArrayList<ImageItem> usermedia) {

        gridAdapter.addAll(usermedia);

        //onScrollListener for gridview, everytime the user moves down new data is loaded
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int myLastVisiblePos = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    userScrolled = true;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                int currentFirstVisPos = view.getFirstVisiblePosition();

                //To only send a new request when user has scrolled down until reach the bottom and while the totalitemcount is lesser than the number of posts
                if (firstVisibleItem + visibleItemCount >= totalItemCount && userScrolled
                        && currentFirstVisPos > myLastVisiblePos && totalItemCount < nposts) {
                    userScrolled = false;
                    objprofile.getProfileMedia(profileFragment, mAccessToken, mClientId,gridAdapter);
                }
                myLastVisiblePos = currentFirstVisPos;
            }
        });





    }


    //Default methods when creating new Fragment

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
