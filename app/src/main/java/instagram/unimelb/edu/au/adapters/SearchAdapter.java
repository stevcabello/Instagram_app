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
import instagram.unimelb.edu.au.models.Search;
import instagram.unimelb.edu.au.utils.Utils;


/**
 * Created by Angela on 9/26/2015.
 */
public class SearchAdapter extends ArrayAdapter<Search> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Search> data = new ArrayList();

    public SearchAdapter(Context context, int resource, ArrayList<Search> data) {
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
    public Search getItem (int position){
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public int getPosition(Search item) {
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
            holder.profilepic = (ImageView) row.findViewById(R.id.iv_profilepic);
            holder.username = (TextView) row.findViewById(R.id.tv_username);
            holder.fullname = (TextView) row.findViewById(R.id.tv_fullname);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Search item =(Search) data.get(position);

        Bitmap profilepic_bitmap = Utils.getBitmap(item.getProfilepic());
        holder.profilepic.setImageBitmap(profilepic_bitmap);

        String username = "<b>"+ item.getUsername() +"</b>";
        holder.username.setText(Html.fromHtml(username));

        holder.fullname.setText(item.getFull_name());


        return row;
    }

    static class ViewHolder {
        ImageView profilepic;
        TextView username;
        TextView fullname;
    }
}
