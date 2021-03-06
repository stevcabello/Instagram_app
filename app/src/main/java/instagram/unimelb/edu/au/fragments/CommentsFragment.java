package instagram.unimelb.edu.au.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.adapters.CommentAdapter;
import instagram.unimelb.edu.au.adapters.UserFeedAdapter;
import instagram.unimelb.edu.au.businessobject.boComments;
import instagram.unimelb.edu.au.models.Comments;
import instagram.unimelb.edu.au.models.UserFeed;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;

/**
 * Fragment of the comments from userfeed's posts
 */
public class CommentsFragment extends Fragment {

    private static Comments userComment;
    private static ArrayList<Comments> userfeedComments;
    private static String media_id;
    private static String access_token;
    private static Context context;
    private static UserFeedAdapter userfeedAdapter;
    private static UserFeed userfeedItem;
    private static Integer userfeedItemposition;

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private static CommentAdapter adapter;
    private ListView listView;
    private String TAG = "CommentsFragment";

    private ImageButton sendComment;
    private EditText writeComment;

    private boComments objComments;


    public CommentsFragment commentsFragment;


    /**
     * Constructor of the CommentsFragment
     * @param user_comment comment to be added
     * @param comments previous post's comments
     * @param id id of the post
     * @param token access_token of the applicaion
     * @param ctx fragment context
     * @param adapter comment's listview adapter
     * @param pos positon of the comment in the comments' list
     * @return
     */
    public static CommentsFragment newInstance(Comments user_comment, ArrayList<Comments> comments, String id, String token, Context ctx, UserFeedAdapter adapter, Integer pos) {
        CommentsFragment fragment = new CommentsFragment();
        userComment = user_comment;
        userfeedComments = comments;
        media_id = id;
        access_token = token;
        context = ctx;
        userfeedAdapter = adapter;
        userfeedItemposition = pos;

        return fragment;
    }

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //To keep the view
        if (rootView != null) {
            return rootView;
        }

        commentsFragment = this;

        //Hide the main tabs
        TabLayout tabLayout = (TabLayout)Globals.mainActivity.findViewById(R.id.tabs);
        tabLayout.setVisibility(View.GONE);

        Globals.mainActivity.setVisibleFragment(this);
        setHasOptionsMenu(true); //to enable the settings action button

        //Reset the previous toolbar settings
        Globals.mainActivity.getSupportActionBar().setTitle(null);
        Globals.mainActivity.getSupportActionBar().setLogo(null);

        //To show the back button
        Globals.mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Globals.mainActivity.getSupportActionBar().setTitle("COMMENTS");



        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_comments,container,false);

        ArrayList<Comments> comments= new ArrayList<>();

        //Only if the user post the media file with a description (text) it is passed as a comment too
        if (userComment.getText()!="")
            comments.add(userComment);


        //Add the comments, but the first row will be the user's text
        adapter = new CommentAdapter(getActivity(),R.layout.item_comment ,comments);
        // Attach the adapter to a ListView
        listView = (ListView) rootView.findViewById(R.id.lv_comments);
        listView.setAdapter(adapter);

        //Add the comments received for the user's post
        adapter.addAll(userfeedComments);

        objComments = new boComments();

        writeComment = (EditText) rootView.findViewById(R.id.et_addcomment);
        sendComment = (ImageButton) rootView.findViewById(R.id.btn_addcomment);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = writeComment.getText().toString();
                if (comment != ""){
                    objComments.sendComment(commentsFragment, access_token, media_id, writeComment.getText().toString(), adapter);
                    writeComment.setText("");
                }
                else Toast.makeText(getActivity(), "Comment is empty!", Toast.LENGTH_SHORT).show();
            }
        });


        return rootView;
    }


    /**
     * Simulates the leave of a comment.
     * @param commentText
     */
    public static void simulateLeavingComment(String commentText) {

        //Get Imageview from url profile image
        ImageView profilepic = new ImageView(context);
        ImageRequest.makeImageRequest(Globals.profile.getProfilepic_url(), context, profilepic, adapter);

        //Get current time
        Long current_time = System.currentTimeMillis() / 1000L;

        //Create a comment
        Comments comment = new Comments(Globals.profile.getUsername(),commentText,String.valueOf(current_time),profilepic, Globals.profile.getProfilepic_url());

        //Update the comments's listview
        adapter.addAll(comment);

        // Get the userfeed item from the position
        UserFeed userfeedItem = userfeedAdapter.getItem(userfeedItemposition);

        //Get the comments from that user's post
        ArrayList<Comments> userfeedItemComments= userfeedItem.getComments();

        //Add the new comment into the comments' list
        userfeedItemComments.add(comment);

        //Set the new list of comments to the user's post
        userfeedItem.setComments(userfeedItemComments);

        //Get the number of comments, add 1 and set the new number of comments
        Integer numberComments = userfeedItem.getNumcomments();
        userfeedItem.setNumcomments(numberComments + 1);


    }


    //Default methods when creating new fragment

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
