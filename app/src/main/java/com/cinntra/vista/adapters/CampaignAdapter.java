package com.cinntra.vista.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cinntra.vista.R;
import com.cinntra.vista.fragments.CampaignDetailFragment;
import com.cinntra.vista.globals.Globals;
import com.cinntra.vista.model.CampaignModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.ViewHolder> {
    Context context;
    List<CampaignModel> leadValueList;
    public CampaignAdapter(Context c, List<CampaignModel> leadValueList) {
        this.context =c;
        this.leadValueList = leadValueList;
        this.tempList=new ArrayList<>();
        tempList.addAll(leadValueList);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.campaign_adapter_screen,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CampaignModel lv = leadValueList.get(position);
        holder.customerName.setText(lv.getCampaignSetName());
        holder.date.setText(lv.getCreateDate());

        if(lv.getStatus() == 0){
            holder.cardNumber.setText(": InActive");
            holder.cardNumber.setTextColor(ContextCompat.getColor(context, R.color.red));
        }else{
            holder.cardNumber.setText(": Active");
            holder.cardNumber.setTextColor(ContextCompat.getColor(context, R.color.green));
        }

        holder.assigned_view.setVisibility(View.VISIBLE);
        holder.assigned.setText(": " +lv.getCampaignAccess());

        // holder.amount.setText("Rs:" + lv.getTurnover());

    }








    @Override
    public int getItemCount() {
        return leadValueList.size();
    }

    List<CampaignModel>  tempList =null ;
    public void filter(String charText)
    {
        charText = charText.toLowerCase(Locale.getDefault());
        leadValueList.clear();
        if (charText.length() == 0) {
            leadValueList.addAll(tempList);
        } else {
            for (CampaignModel st : tempList) {
                if(st.getCampaignSetName()!=null&&!st.getCampaignSetName().isEmpty()) {
                    if (st.getCampaignSetName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        leadValueList.add(st);
                    }
                }
            }

        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView customerName,cardNumber,date,assigned;
        LinearLayout assigned_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.item_title);
            cardNumber   = itemView.findViewById(R.id.price);
            date   = itemView.findViewById(R.id.item_date);
            assigned   = itemView.findViewById(R.id.assigned);
            assigned_view   = itemView.findViewById(R.id.assigned_view);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle b = new Bundle();
                    b.putSerializable(Globals.CampaignData, leadValueList.get(getAdapterPosition()));
                    CampaignDetailFragment fragment = new CampaignDetailFragment();
                    fragment.setArguments(b);
                    FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.customer_lead, fragment);
                    transaction.addToBackStack("Back");
                    transaction.commit();
                }
            });


        }

    }
}