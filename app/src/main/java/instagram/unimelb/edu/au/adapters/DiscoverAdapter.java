package instagram.unimelb.edu.au.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
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


public class DiscoverAdapter extends ArrayAdapter<ImageItem> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList();

    public DiscoverAdapter(Context context, int resource, ArrayList data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((FragmentActivity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.iv_discover_photo);
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
