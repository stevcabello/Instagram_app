package instagram.unimelb.edu.au.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.models.FollowerActivityFeed;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.models.YouActivityFeed;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Utils;

public class FollowingActivityFeedAdapter extends ArrayAdapter<FollowerActivityFeed> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<FollowerActivityFeed> data = new ArrayList<FollowerActivityFeed>();

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
    public FollowerActivityFeed getItem (int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getPosition(FollowerActivityFeed item) {
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
            //holder.created_time = (TextView) row.findViewById(R.id.tv_timestamp);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        FollowerActivityFeed item = (FollowerActivityFeed) data.get(position);

        Bitmap profilepic_bitmap = Utils.getBitmap(item.getProfilepic());
        holder.profilepic.setImageBitmap(profilepic_bitmap);
        holder.description.setText(Html.fromHtml("<b>" + item.getUsername() + "</b>" + " " + item.getFullname()));



        return row;
    }

    static class ViewHolder {
        ImageView profilepic;
        TextView description;

    }

}
