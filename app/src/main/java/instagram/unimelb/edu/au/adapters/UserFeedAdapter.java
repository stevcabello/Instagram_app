package instagram.unimelb.edu.au.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.fragments.CommentsFragment;
import instagram.unimelb.edu.au.fragments.LikesFragment;
import instagram.unimelb.edu.au.models.Comments;
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

    public UserFeedAdapter(Context context,int resource, String access_token ,ArrayList data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
        this.access_token = access_token;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

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
            holder.ibtn_likes = (ImageButton) row.findViewById(R.id.ibtn_like);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final UserFeed item = (UserFeed) data.get(position);

        holder.comments.removeAllViews(); //to remove the previous TextViews added into the Linear Layout

        Bitmap photo_bitmap = Utils.getBitmap(item.getPhoto());
        holder.uploadedphoto.setImageBitmap(photo_bitmap);
        holder.uploadedphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context,"post by " + username,Toast.LENGTH_SHORT).show();
                ImageView imageView = new ImageView(context);
                ImageRequest.makeImageRequest(item.getPhoto_url(),context,imageView,UserFeedAdapter.this);
                item.setPhoto(imageView);
            }
        });

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
        if (description=="")
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


        //Handles giving likes
        holder.ibtn_likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Provide functionality
                Toast.makeText(context,"Giving like to the user's post",Toast.LENGTH_SHORT).show();
            }
        });

        return row;
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
        ImageButton ibtn_likes;
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

}
