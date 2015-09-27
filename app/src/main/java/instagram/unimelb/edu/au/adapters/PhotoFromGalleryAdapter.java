package instagram.unimelb.edu.au.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.models.ImageItem;

public class PhotoFromGalleryAdapter extends ArrayAdapter<ImageItem> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();
    //private boProfile objProfile;

    public PhotoFromGalleryAdapter(Context context, int resource, ArrayList<ImageItem> data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
        //objProfile = new boProfile();
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
            //LayoutInflater inflater = ((FragmentActivity) context).getLayoutInflater();
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.iv_image_gallery);
            row.setTag(holder);

        } else {

            holder = (ViewHolder) row.getTag();

        }


        holder.image.setAdjustViewBounds(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.image.setLayoutParams(layoutParams);

        ImageItem item = getItem(position);

        //objProfile.makeImageRequest(item.getImageurl(), context, holder.image); //not in use due to make the gridview repeat images

        //holder.image.setImageBitmap(item.getImage()); //not in use due to sometimes the bitmap is empty

       // Bitmap bitmap = Utils.getBitmap(item.getImageview());
       // holder.image.setImageBitmap(bitmap);
        Picasso.with(getContext())
                .load(item.getImageurl())
                .resize(150,150)
                .into(holder.image);
        Log.i("PhotofromGallery", "Adapter" + item.getImageurl());


        return row;
    }

    static class ViewHolder {
        ImageView image;
    }

}