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
 * Created by Angela on 9/24/2015.
 */
public class SubFollowingActivityFeedAdapter extends ArrayAdapter<ImageItem> {

        private Context context;
        private int layoutResourceId;
        private ArrayList<ImageItem> data = new ArrayList<ImageItem>();
        //private boProfile objProfile;

        public SubFollowingActivityFeedAdapter(Context context, int resource, ArrayList<ImageItem> data) {
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
                holder.image = (ImageView) row.findViewById(R.id.iv_following_photo);
                row.setTag(holder);

            } else {

                holder = (ViewHolder) row.getTag();

            }


            holder.image.setAdjustViewBounds(true);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.image.setLayoutParams(layoutParams);

            ImageItem item = getItem(position);

            //objProfile.makeImageRequest(item.getImageurl(), context, holder.image); //not in use due to make the gridview repeat images

            //holder.image.setImageBitmap(item.getImage()); //not in use due to sometimes the bitmap is empty

            Bitmap bitmap = Utils.getBitmap(item.getImageview());
            holder.image.setImageBitmap(bitmap);



            return row;
        }

        static class ViewHolder {
            ImageView image;
        }

    }


