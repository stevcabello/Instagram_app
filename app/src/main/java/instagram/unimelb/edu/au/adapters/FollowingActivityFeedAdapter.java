package instagram.unimelb.edu.au.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.models.FollowingActivityFeed;
import instagram.unimelb.edu.au.utils.Utils;

public class FollowingActivityFeedAdapter extends ArrayAdapter<FollowingActivityFeed> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<FollowingActivityFeed> data = new ArrayList<FollowingActivityFeed>();
    private SubFollowingActivityFeedAdapter subgridAdapter;

    public FollowingActivityFeedAdapter(Context context, int resource, ArrayList data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public FollowingActivityFeed getItem (int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getPosition(FollowingActivityFeed item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((FragmentActivity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.profilepic = (ImageView) row.findViewById(R.id.iv_photo);
            holder.description = (TextView) row.findViewById(R.id.tv_description);
            holder.imageItems = (GridView) row.findViewById(R.id.gv_photos);
            //holder.created_time = (TextView) row.findViewById(R.id.tv_timestamp);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        FollowingActivityFeed item = (FollowingActivityFeed) data.get(position);
        if (item.getProfilepic()!=null){
        Bitmap profilepic_bitmap = Utils.getBitmap(item.getProfilepic());
        holder.profilepic.setImageBitmap(profilepic_bitmap);
        holder.description.setText(Html.fromHtml("<b>" + item.getUsername() + "</b>" + " " + item.getFullname()));
            subgridAdapter = new SubFollowingActivityFeedAdapter(context,R.layout.subitem_followingactivityfeed, item.getImageItems());
           holder.imageItems.setAdapter(subgridAdapter);
        }


        return row;
    }

    static class ViewHolder {
        ImageView profilepic;
        TextView description;
        GridView imageItems;
    }

}
