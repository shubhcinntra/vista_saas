package com.cinntra.vista.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cinntra.vista.R;
import com.cinntra.vista.model.UTypeData;
import com.cinntra.vista.newapimodel.ResponseQuoteListDropDown;

import java.util.List;


public class QuotationDropDownSpinnerAdapter extends BaseAdapter {
    Context context;
    List<ResponseQuoteListDropDown.Datum> stagesList;
    LayoutInflater inflter;
   public QuotationDropDownSpinnerAdapter(Context context, List<ResponseQuoteListDropDown.Datum> stagesList)
      {
   this.context    = context;
   this.stagesList = stagesList;
   inflter = (LayoutInflater.from(context));
      }
    @Override
    public int getCount() {
        return stagesList.size();
    }

    @Override
    public ResponseQuoteListDropDown.Datum getItem(int position) {
        return stagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v = inflter.inflate(R.layout.stages_spinner_item, null);
        TextView title = (TextView)v.findViewById(R.id.title);
        title.setText(stagesList.get(position).getU_QUOTNM());
        return v;
    }
}
