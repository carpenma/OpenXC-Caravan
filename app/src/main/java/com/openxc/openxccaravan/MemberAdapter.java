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

import java.util.ArrayList;

/**
 * Created by MCARPE53 on 10/27/2016.
 */

public class MemberAdapter extends ArrayAdapter<Member> {
    private final Context context;
    private static final String TAG = "MemberAdapter";
    private final int layoutResourceId;
    private final ArrayList<Member> member_list;
    private int select_idx;

    public MemberAdapter(Context context, int layoutResourceId, ArrayList<Member> member_list) {
        super(context, layoutResourceId);
        this.layoutResourceId = layoutResourceId;
        this.member_list = member_list;
        this.context = context;
    }

    public void setSelectedIdx(int idx) {
        select_idx = idx;
        notifyDataSetChanged();
    }

    public int getSelectedIdx() {
        return select_idx;
    }

    @Override
    public @NonNull
    View getView(int pos, View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        MemberAdapter.ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MemberAdapter.ViewHolder();
            holder.pretty = (TextView)row.findViewById(R.id.PrettyName);
            holder.details = (TextView)row.findViewById(R.id.Details) ;

            row.setTag(holder);
        }
        else {
            holder = (MemberAdapter.ViewHolder)row.getTag();
        }

        Member vehicle = member_list.get(pos);

        holder.pretty.setText(vehicle.pretty_name);
        holder.details.setText(vehicle.year+" "+vehicle.make+" "+vehicle.model);

        return row;
    }

    static class ViewHolder {
        TextView pretty;
        TextView details;
    }
}