package com.example.transvision.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.transvision.R;
import com.example.transvision.model.EquipmentDetails;

import java.util.ArrayList;
import java.util.List;

public class ViewApprovalAdapter extends RecyclerView.Adapter<ViewApprovalAdapter.Approvals_ViewHolder> implements Filterable {
    private Context context;
    private List<EquipmentDetails> arrayList;
    private List<EquipmentDetails> filteredList;


    public ViewApprovalAdapter(Context context, List<EquipmentDetails> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.filteredList = arrayList;
    }

    @NonNull
    @Override
    public Approvals_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approval_adapter, viewGroup, false);
        return new Approvals_ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Approvals_ViewHolder approvals_viewHolder, int i) {
        approvals_viewHolder.tv_id.setText(filteredList.get(i).getID());
        approvals_viewHolder.tv_user_name.setText(filteredList.get(i).getUSER_NAME());
        approvals_viewHolder.tv_equipments.setText(filteredList.get(i).getITEMS());
        approvals_viewHolder.tv_mob_no.setText(filteredList.get(i).getMOBILE_NO());
        approvals_viewHolder.tv_sudiv_code.setText(filteredList.get(i).getSUBDIV_CODE());
        approvals_viewHolder.tv_req_date.setText(filteredList.get(i).getREQUESTED_DATE());
        if (!TextUtils.isEmpty(filteredList.get(i).getREMARK())) {
            approvals_viewHolder.tv_reamrk.setText(filteredList.get(i).getREMARK());
        } else approvals_viewHolder.remark.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(filteredList.get(i).getAPPROVED_DATE())) {
            approvals_viewHolder.tv_app_date.setText(filteredList.get(i).getAPPROVED_DATE());
        } else approvals_viewHolder.tv_app_date.setText("Pending");
        if (!TextUtils.isEmpty(filteredList.get(i).getRECEIVED_DATE())) {
            approvals_viewHolder.tv_rec_date.setText(filteredList.get(i).getRECEIVED_DATE());
        } else approvals_viewHolder.tv_rec_date.setText("Pending");
        approvals_viewHolder.management.setVisibility(View.GONE);
        approvals_viewHolder.received_btn.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    //*********************************************** Filter *************************************************************
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search = constraint.toString();
                if (search.isEmpty())
                    filteredList = arrayList;
                else {
                    ArrayList<EquipmentDetails> filterlist = new ArrayList<>();
                    for (int i = 0; i < arrayList.size(); i++) {
                        EquipmentDetails equipmentDetails = arrayList.get(i);
                        if (equipmentDetails.getID().contains(search)) {
                            filterlist.add(equipmentDetails);
                        }
                    }
                    filteredList = filterlist;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<EquipmentDetails>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    class Approvals_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_id, tv_user_name, tv_equipments, tv_mob_no, tv_sudiv_code, tv_reamrk, tv_req_date, tv_app_date, tv_rec_date, tv_call;
        LinearLayout main_layout, management, remark, approved, received,received_btn;

        Approvals_ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.txt_id);
            tv_user_name = itemView.findViewById(R.id.txt_user_name);
            tv_equipments = itemView.findViewById(R.id.txt_equipments);
            tv_mob_no = itemView.findViewById(R.id.txt_mob_no);
            tv_sudiv_code = itemView.findViewById(R.id.txt_subdiv_code);
            tv_reamrk = itemView.findViewById(R.id.txt_remark);
            tv_req_date = itemView.findViewById(R.id.txt_req_date);
            tv_app_date = itemView.findViewById(R.id.txt_approved_date);
            tv_rec_date = itemView.findViewById(R.id.txt_received_date);
            management = itemView.findViewById(R.id.lin_management);
            main_layout = itemView.findViewById(R.id.lin_main_layout);
            remark = itemView.findViewById(R.id.lin_remark);
            approved = itemView.findViewById(R.id.lin_approved_date);
            received = itemView.findViewById(R.id.lin_received_date);
            received_btn = itemView.findViewById(R.id.lin_received_btn);
            tv_call = itemView.findViewById(R.id.txt_call);
            tv_call.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (v.getId() == R.id.txt_call) {
                String mobile_no = arrayList.get(pos).getMOBILE_NO();
                if (!TextUtils.isEmpty(mobile_no)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mobile_no, null));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Mobile number is not available.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
