package instagram.unimelb.edu.au.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import instagram.unimelb.edu.au.R;
import instagram.unimelb.edu.au.models.UserFeed;
import instagram.unimelb.edu.au.networking.ImageRequest;
import instagram.unimelb.edu.au.utils.Utils;

/**
 * Handles the viewpager of userfeed post's photos
 */
public class ListViewItemViewPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    UserFeedAdapter mAdapter;
    UserFeed item;


    public ListViewItemViewPagerAdapter(Context context, UserFeedAdapter adapter, UserFeed userFeed) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAdapter = adapter;
        item = userFeed;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        View itemView = mLayoutInflater.inflate(R.layout.pager_item, collection, false);

        ImageView uploadedPhoto = (ImageView) itemView.findViewById(R.id.iv_photo);
        if (position==1) {
            Bitmap photo_bitmap = Utils.getBitmap(item.getPhoto());
            uploadedPhoto.setImageBitmap(photo_bitmap);
            uploadedPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //In case the image doesn't load properly
                    ImageView imageView = new ImageView(mContext);
                    ImageRequest.makeImageRequest(item.getPhoto_url(), mContext, imageView, mAdapter);
                    item.setPhoto(imageView);
                }
            });
        }
        else uploadedPhoto.setImageResource(R.drawable.white_bg);

        collection.addView(itemView);

        return itemView;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (LinearLayout)object;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((LinearLayout)view);
    }
}
