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
import instagram.unimelb.edu.au.models.Comments;
import instagram.unimelb.edu.au.utils.Utils;

/**
 * Manages the Comment's listview data
 */
public class CommentAdapter extends ArrayAdapter<Comments> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Comments> data = new ArrayList();

    public CommentAdapter(Context context,int resource, ArrayList data) {
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
            holder.profilepic = (ImageView) row.findViewById(R.id.iv_profilepic);
            holder.created_time = (TextView) row.findViewById(R.id.tv_created_time);
            holder.comment = (TextView) row.findViewById(R.id.tv_comment);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Comments item = data.get(position);

        //Set the profile pic
        Bitmap profilepic_bitmap = Utils.getBitmap(item.getProfilepic());
        holder.profilepic.setImageBitmap(profilepic_bitmap);

        //Set the username with its comment
        String username = "<font color='#0000A0'><b>"+ item.getUsername() +"</b></font>";
        String description = item.getText();
        holder.comment.setText(Html.fromHtml(username + " " + description));

        //Set the timestamp in long mode(e.g. x minutes ago)
        holder.created_time.setText(Utils.getElapsedtime(item.getCreated_time(),"long"));


        return row;
    }

    static class ViewHolder {
        ImageView profilepic;
        TextView comment;
        TextView created_time;
    }
}
