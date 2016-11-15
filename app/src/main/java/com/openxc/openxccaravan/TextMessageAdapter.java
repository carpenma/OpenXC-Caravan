package com.openxc.openxccaravan;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.openxcplatform.openxccaravan.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by MCARPE53 on 11/15/2016.
 */

public class TextMessageAdapter extends ArrayAdapter<TextMessage> {
    private final Context context;
    private static final String TAG = "TextMessageAdapter";
    private final int layoutResourceId;
    private final ArrayList<TextMessage> text_message_list;

    public TextMessageAdapter(Context context, int layoutResourceId, ArrayList<TextMessage> text_message_list) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.text_message_list = text_message_list;
        this.context = context;
    }

    @Override
    public int getCount() {
        super.getCount();
        return text_message_list.size();
    }

    @Override
    public @NonNull
    View getView(int pos, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        TextMessageAdapter.ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TextMessageAdapter.ViewHolder();

            row.setTag(holder);
        }
        else {
            holder = (TextMessageAdapter.ViewHolder)row.getTag();
        }

        TextMessage msg = text_message_list.get(pos);

        holder.body.setText(msg.body.replace("%20", " "));
        holder.sender.setText(msg.sender.replace("%20", " "));

        //Handle conversion of timestamp to human-readable time
            // At some point should also check if the date the message was sent (in local time) is different than the current day.  If so, the date should be included as well
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String send_time = sdf.format(new Date(msg.time));

        holder.time.setText(send_time);

        return row;
    }

    static class ViewHolder {
        TextView body;
        TextView sender;
        TextView time;
    }
}
