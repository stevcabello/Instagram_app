package instagram.unimelb.edu.au.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.models.SuggestedFriends;
import instagram.unimelb.edu.au.utils.Utils;

/**
 * Class that permit to charge all the information get on the boDiscover
 * to the suggested friends fragment.
 */
public class SuggestedFriendsAdapter extends ArrayAdapter<SuggestedFriends>{
    private Context context;
    private int layoutResourceId;
    private ArrayList<SuggestedFriends> data = new ArrayList<SuggestedFriends>();
    private SubSuggestedFriendsAdapter subgridAdapter;

    public SuggestedFriendsAdapter(Context context, int resource, ArrayList data) {
        super(context, resource, data);
        this.context = context;
        this.layoutResourceId = resource;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public SuggestedFriends getItem (int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getPosition(SuggestedFriends item) {
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

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        SuggestedFriends item = (SuggestedFriends) data.get(position);

        Log.i("AdapterFriends",item.getUsername());

        if (item.getProfilepic()!=null){


            Bitmap profilepic_bitmap = Utils.getBitmap(item.getProfilepic());
            holder.profilepic.setImageBitmap(profilepic_bitmap);
            holder.description.setText(Html.fromHtml("<font color='#0000A0'><b>" + item.getUsername() + "</b></font>" + "<br/>" + "<font size=5>" +  item.getFullname() + "</font>"));
            subgridAdapter = new SubSuggestedFriendsAdapter(context,R.layout.subitem_suggested_friends, item.getImageItems());
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
