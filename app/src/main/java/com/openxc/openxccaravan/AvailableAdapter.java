package com.openxc.openxccaravan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.openxc.openxccaravan.Available;
import com.openxcplatform.openxccaravan.R;

import java.util.ArrayList;

/**
 * Created by MCARPE53 on 10/24/2016.
 */

public class AvailableAdapter extends ArrayAdapter<Available>{
    private final Context context;
    private final int layoutResourceId;
    private final ArrayList<Available> available_list;

    public AvailableAdapter(Context context, int layoutResourceId, ArrayList<Available> available_list) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.available_list = available_list;
        this.context = context;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
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

        holder.pretty.setText(caravan.pretty_name);
        holder.details.setText("Locked?: "+caravan.locked+" | "+caravan.count+"/"+caravan.max);

        return row;
    }

    static class ViewHolder {
        TextView pretty;
        TextView details;
    }
}
