package com.cinntra.vista.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cinntra.vista.R;
import com.cinntra.vista.model.ContactPersonData;

import java.util.List;


public class ContactPersonAdapter extends BaseAdapter {
    Context context;
    List<ContactPersonData> stagesList;
    LayoutInflater inflter;

    public ContactPersonAdapter(Context context, List<ContactPersonData> stagesList) {
        this.context = context;
        this.stagesList = stagesList;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return stagesList.size();
    }


    @Override
    public ContactPersonData getItem(int position) {
        return stagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v = inflter.inflate(R.layout.stages_spinner_item, null);
        TextView title = (TextView) v.findViewById(R.id.title);
        if (stagesList.size() > 0) {
            title.setText(stagesList.get(position).getFirstName());
        }

        return v;
    }
}
