package instagram.unimelb.edu.au.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import instagram.unimelb.edu.au.R;

public class PhotoFromGalleryAdapter extends ArrayAdapter<ArrayList<String>> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();


    public PhotoFromGalleryAdapter(Context context, int resource, ArrayList<ArrayList<String>> data) {
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
    public ArrayList<String> getItem (int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getPosition(ArrayList<String> item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.iv_image_gallery);
            row.setTag(holder);

        } else {

            holder = (ViewHolder) row.getTag();

        }


        holder.image.setAdjustViewBounds(true);

        ArrayList<String> item = getItem(position);

      //Load the image on the image view
        Picasso.with(getContext())
                    .load("file://" + item.get(1))
                    .resize(150, 150)
                    .into(holder.image);
        Log.i("PhotofromGallery", "Adapter" + item.get(1));
        return row;
    }

    static class ViewHolder {
        ImageView image;
    }

}
