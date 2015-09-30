package instagram.unimelb.edu.au.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.businessobject.boLikes;
import instagram.unimelb.edu.au.fragments.CommentsFragment;
import instagram.unimelb.edu.au.fragments.LikesFragment;
import instagram.unimelb.edu.au.models.Comments;
import instagram.unimelb.edu.au.models.Likes;
import instagram.unimelb.edu.au.models.UserFeed;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Globals;
import instagram.unimelb.edu.au.utils.Utils;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;


public class UserFeedAdapter extends ArrayAdapter<UserFeed> implements StickyListHeadersAdapter{

    private Context context;
    private int layoutResourceId;
    private ArrayList<UserFeed> data = new ArrayList();
    private String access_token;
    private boLikes objLikes = new boLikes();
    private int like_position;
    ListViewItemViewPagerAdapter swipeAdapter;
    ViewHolder holder = null;

    public UserFeedAdapter(Context context,int resource, String access_token ,ArrayList data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
        this.access_token = access_token;


    }

    //Use these two methods to avoid the recycled views, specifically when user give a like to avoid
    //to avoid that other likes (hearts) turn red too.

    @Override
    public int getViewTypeCount() {
        return 500; //it should be data.size(), but as in the instance the adapter is created with no data we put just a large number
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        View row = convertView;
        //ViewHolder holder = null;
        holder = null;
        final boolean[] myChecks = new boolean[data.size()];

        if (row == null) {
            LayoutInflater inflater = ((FragmentActivity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.uploadedphoto = (ImageView) row.findViewById(R.id.iv_photo);
            holder.numberlikes = (Button) row.findViewById(R.id.btn_likesnumber);
            holder.description = (TextView) row.findViewById(R.id.tv_description);
            holder.comments = (LinearLayout) row.findViewById(R.id.lly_comments);
            holder.morecomments = (Button) row.findViewById(R.id.btn_morecomments);
            holder.btn_addcomment = (Button) row.findViewById(R.id.btn_addcomment);
            holder.ibtn_addcomment = (ImageButton) row.findViewById(R.id.ibtn_comment);
            holder.tbtn_likes = (ToggleButton) row.findViewById(R.id.ibtn_like);
            holder.viewPager=(ViewPager)row.findViewById(R.id.pager);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final UserFeed item = (UserFeed) data.get(position);

        holder.comments.removeAllViews(); //to remove the previous TextViews added into the Linear Layout

        //Now it is used in ListViewItemViewPagerAdapter

//        Bitmap photo_bitmap = Utils.getBitmap(item.getPhoto());
//        holder.uploadedphoto.setImageBitmap(photo_bitmap);
//        holder.uploadedphoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(context,"post by " + username,Toast.LENGTH_SHORT).show();
//                ImageView imageView = new ImageView(context);
//                ImageRequest.makeImageRequest(item.getPhoto_url(), context, imageView, UserFeedAdapter.this);
//                //ImageRequest.makeImageRequest(item.getPhoto_url(),context,imageView,);
//                item.setPhoto(imageView);
//            }
//        });


        Integer numlikes = item.getNumLikes();
        String format_numlikes = "<font color='#0000A0'><b>"+ String.valueOf(numlikes) +" likes</b></font>";
        if (numlikes==0)
            holder.numberlikes.setText("");
        else {
            holder.numberlikes.setText(Html.fromHtml(format_numlikes));
            holder.numberlikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLikes(item);
                }
            });
        }

        final String username = "<font color='#0000A0'><b>"+ item.getUsername() +"</b></font>";

        String description = item.getDescription();
        if (description.equals(""))
            holder.description.setText(description);
        else
            holder.description.setText(Html.fromHtml(username + " " + description));

        Integer numcomments = item.getNumcomments();
        final ArrayList<Comments> arrayComments = item.getComments();

        Integer comments_to_display;

        //To only display as maximum 3 comments, if more than 3 comments then the text View all the comments is shown.
        if (numcomments > 3) {
            comments_to_display = 3;
            holder.morecomments.setVisibility(View.VISIBLE);
            holder.morecomments.setText("view all " + numcomments + " comments");
            holder.morecomments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openComments(item);
                }
            });
        }else {
            comments_to_display = arrayComments.size();
            holder.morecomments.setVisibility(View.GONE);
        }

        for (int i=0;i<comments_to_display;i++){
            TextView textView = new TextView(context);
            String comment_username = "<font color='#0000A0'><b>"+ arrayComments.get(i).getUsername() +"</b></font>";
            textView.setText(Html.fromHtml(comment_username + " " + arrayComments.get(i).getText()));
            holder.comments.addView(textView);
        }


        //Handles adding comments
        holder.btn_addcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComments(item);
            }
        });

        holder.ibtn_addcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComments(item);
            }
        });


        //Handles set like to user's post
        holder.tbtn_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLike(v, item);
            }
        });


        swipeAdapter = new ListViewItemViewPagerAdapter(context,this,item);
        holder.viewPager.setAdapter(swipeAdapter);
        holder.viewPager.setCurrentItem(1);
        //Using the depracated method instead of addOnPageChangeListener due to the latter causes to many repetitions of the AlertDialog (OnPageSelected sensed many times)
        holder.viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0 || position == 2) {
                    goBack();
                   // Bluetooth.displayPromptForSendingPhoto(Globals.mainActivity); // Ask to user permission to share the post
                }
            }

        });




        return row;
    }


    /**
     * Go back to the post's image when user swipe from right to left or viceversa
     */
    public void goBack(){
        holder.viewPager.setCurrentItem(1);
        notifyDataSetChanged();
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.header_userfeed, parent, false);
            holder.profilepic = (ImageView) convertView.findViewById(R.id.iv_tile_photo);
            holder.username = (TextView) convertView.findViewById(R.id.tv_tile_username);
            holder.location = (TextView) convertView.findViewById(R.id.tv_location);
            holder.created_time = (TextView) convertView.findViewById(R.id.tv_timestamp);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        final UserFeed item = (UserFeed) data.get(position);

        Bitmap profilepic_bitmap = Utils.getBitmap(item.getProfilepic());
        holder.profilepic.setImageBitmap(profilepic_bitmap);

        final String username = "<font color='#0000A0'><b>"+ item.getUsername() +"</b></font>";
        holder.username.setText(Html.fromHtml(username));

        holder.location.setText(item.getLocation());

        holder.created_time.setText(Utils.getElapsedtime(item.getCreated_time(), "short"));


        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return position;
    }


    static class HeaderViewHolder {
        ImageView profilepic;
        TextView username;
        TextView location;
        TextView created_time;
    }

    static class ViewHolder {
        ImageView uploadedphoto;
        Button numberlikes;
        TextView description;
        LinearLayout comments;
        Button morecomments;
        Button btn_addcomment;
        ImageButton ibtn_addcomment;
        ToggleButton tbtn_likes;
        ViewPager viewPager;
    }


    /**
     * Show the comment's view of the userfeed item passed as @param.
     * @param item
     */
    public void openComments(UserFeed item) {

        //The first row of comments will be the comment or description posted by the user
        Comments comments = new Comments(item.getUsername(),item.getDescription(),item.getCreated_time(),item.getProfilepic());

        //Show the comments received for the user's post
        FragmentTransaction fragmentTransaction = Globals.mainActivity.getSupportFragmentManager().beginTransaction();
        CommentsFragment cf= CommentsFragment.newInstance(comments, item.getComments(),item.getMedia_id(), access_token, context, this, getPosition(item));
        fragmentTransaction.replace(R.id.fly_userfeed_fragment, cf);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    /**
     * Show the like's view of the userfeed item passed as @param.
     * @param item
     */
    public void openLikes(UserFeed item) {

        //Show the Likes received for the user's post
        FragmentTransaction fragmentTransaction = Globals.mainActivity.getSupportFragmentManager().beginTransaction();
        LikesFragment lf= LikesFragment.newInstance(item.getLikes());
        fragmentTransaction.replace(R.id.fly_userfeed_fragment, lf);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    /**
     *  Handles giving likes : even if the request returns error (due to authorization issue)
     *  The heart image will turn red and the number of likes will increase in one.
     *  Also the info about the user will appears as if he/she really was able to give like.
     * @param item
     */
    public void setLike(View v,UserFeed item) {


        //Toogle between giving like and not giving like
        if (v.getTag().toString().equals("not_like")) {

            //Make post request to give a Like
            objLikes.sendLike(context,access_token,item.getMedia_id());

            //change the tag
            v.setTag("like");

            //Increasing number of likes
            Integer numlikes = item.getNumLikes() + 1;
            item.setNumLikes(numlikes);

            //Adding user to list of people how liked the published media
            ArrayList<Likes> arrLikes = item.getLikes();

            ImageView profile_pic =new ImageView(context);
            ImageRequest.makeImageRequest(Globals.PROFILE_PIC_URL, context, profile_pic, UserFeedAdapter.this);

            Likes like = new Likes(Globals.USERNAME,Globals.FULL_NAME,profile_pic);

            like_position = arrLikes.size();
            arrLikes.add(like_position,like);

            //Set the updated list of likes
            item.setLikes(arrLikes);



        } else {


            //change the tag
            v.setTag("not_like");

            //Decreasing number of likes
            Integer numlikes = item.getNumLikes() - 1;
            item.setNumLikes(numlikes);

            //Removing user from list of people how liked the published media
            ArrayList<Likes> arrLikes = item.getLikes();
            arrLikes.remove(like_position);

            item.setLikes(arrLikes);

            //Make post request for delete the Like
            objLikes.deleteLike(context,access_token,item.getMedia_id());

            notifyDataSetChanged(); //to update the change


        }

    }

}
