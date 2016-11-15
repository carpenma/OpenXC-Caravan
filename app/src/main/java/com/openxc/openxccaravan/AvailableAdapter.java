package com.openxc.openxccaravan;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.openxcplatform.openxccaravan.R;

import java.util.ArrayList;

/**
 * Created by MCARPE53 on 10/24/2016.
 */

public class AvailableAdapter extends ArrayAdapter<Available>{
    private final Context context;
    private static final String TAG = "AvailableAdapter";
    private final int layoutResourceId;
    private final ArrayList<Available> available_list;
    private int select_idx;

    public AvailableAdapter(Context context, int layoutResourceId, ArrayList<Available> available_list) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.available_list = available_list;
        this.context = context;
    }

    public void setSelectedIdx(int idx) {
        select_idx = idx;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        super.getCount();
        return available_list.size();
    }

    public int getSelectedIdx() {
        return select_idx;
    }

    @Override
    public @NonNull View getView(int pos, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.pretty = (TextView)row.findViewById(R.id.PrettyName);
            holder.details = (TextView)row.findViewById(R.id.Details) ;

            row.setTag(holder);
        }
        else {
            holder = (ViewHolder)row.getTag();
        }

        Available caravan = available_list.get(pos);

        holder.pretty.setText(caravan.pretty_name.replace("%20", " "));
        holder.details.setText("Locked?: "+caravan.locked+" | "+caravan.count+"/"+caravan.max);

        return row;
    }

    static class ViewHolder {
        TextView pretty;
        TextView details;
    }
}
