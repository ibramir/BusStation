package com.ibramir.busstation.activities.newtrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ibramir.busstation.R;
import com.ibramir.busstation.users.Driver;

import java.util.List;

class DriverSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<Driver> drivers;

    DriverSpinnerAdapter(Context context, List<Driver> data) {
        this.context = context;
        drivers = data;
    }

    @Override
    public int getCount() {
        return drivers.size();
    }

    @Override
    public Driver getItem(int position) {
        return drivers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if(itemView == null)
            itemView = LayoutInflater.from(context).inflate(R.layout.support_simple_spinner_dropdown_item,parent,false);
        TextView textView = itemView.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getName());
        return itemView;
    }
}
