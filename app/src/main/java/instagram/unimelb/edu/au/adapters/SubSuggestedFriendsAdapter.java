package instagram.unimelb.edu.au.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.models.ImageItem;
import instagram.unimelb.edu.au.utils.Utils;

/**
 *  Adapter of the gridview of images (only 3 images) in the suggested friends fragment. Each gridview belongs to
 *  a suggesteduser.
 */
public class SubSuggestedFriendsAdapter extends ArrayAdapter<ImageItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

    public SubSuggestedFriendsAdapter(Context context, int resource, ArrayList<ImageItem> data) {
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
    public ImageItem getItem (int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getPosition(ImageItem item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.iv_suggestedFriend_photo);
            row.setTag(holder);

        } else {

            holder = (ViewHolder) row.getTag();

        }


        holder.image.setAdjustViewBounds(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.image.setLayoutParams(layoutParams);

        ImageItem item = getItem(position);
        Bitmap bitmap = Utils.getBitmap(item.getImageview());
        holder.image.setImageBitmap(bitmap);

        return row;
    }

    static class ViewHolder {
        ImageView image;
    }
}
