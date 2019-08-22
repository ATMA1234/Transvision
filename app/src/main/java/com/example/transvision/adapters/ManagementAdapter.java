package com.example.transvision.adapters;

import android.Manifest;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transvision.R;
import com.example.transvision.model.EquipmentDetails;

import java.util.ArrayList;

public class ManagementAdapter extends RecyclerView.Adapter<ManagementAdapter.Approvals_ViewHolder> {
    private Context context;
    private ArrayList<EquipmentDetails> arrayList;

    public ManagementAdapter(Context context, ArrayList<EquipmentDetails> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Approvals_ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.approval_adapter, viewGroup, false);
        return new Approvals_ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Approvals_ViewHolder approvals_viewHolder, int i) {
        final EquipmentDetails equipmentDetails = arrayList.get(i);
        if ((arrayList.get(i).getAPPROVED_FLAG()).equals("Y")) {
            approvals_viewHolder.main_layout.setVisibility(View.GONE);
        } else {
            approvals_viewHolder.tv_id.setText(arrayList.get(i).getID());
            approvals_viewHolder.tv_user_name.setText(arrayList.get(i).getUSER_NAME());
            approvals_viewHolder.tv_equipments.setText(arrayList.get(i).getITEMS());
            approvals_viewHolder.tv_mob_no.setText(arrayList.get(i).getMOBILE_NO());
            approvals_viewHolder.tv_sudiv_code.setText(arrayList.get(i).getSUBDIV_CODE());
            approvals_viewHolder.tv_req_date.setText(arrayList.get(i).getREQUESTED_DATE());
            if (!TextUtils.isEmpty(arrayList.get(i).getREMARK())) {
                approvals_viewHolder.tv_reamrk.setText(arrayList.get(i).getREMARK());
            } else approvals_viewHolder.remark.setVisibility(View.GONE);
            approvals_viewHolder.received.setVisibility(View.GONE);
            approvals_viewHolder.approved.setVisibility(View.GONE);
            approvals_viewHolder.received_btn.setVisibility(View.GONE);
        }

        approvals_viewHolder.tv_chiefApproval.setChecked(false);
        approvals_viewHolder.tv_chiefApproval.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                equipmentDetails.setSelected(b);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Approvals_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_id, tv_user_name, tv_equipments, tv_mob_no, tv_sudiv_code, tv_reamrk, tv_req_date, tv_call;
        CheckBox tv_chiefApproval;
        LinearLayout main_layout, management, remark, approved, received, received_btn;

        Approvals_ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.txt_id);
            tv_user_name = itemView.findViewById(R.id.txt_user_name);
            tv_equipments = itemView.findViewById(R.id.txt_equipments);
            tv_mob_no = itemView.findViewById(R.id.txt_mob_no);
            tv_sudiv_code = itemView.findViewById(R.id.txt_subdiv_code);
            tv_reamrk = itemView.findViewById(R.id.txt_remark);
            tv_req_date = itemView.findViewById(R.id.txt_req_date);
            tv_chiefApproval = itemView.findViewById(R.id.txt_chief_approval);
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

    public ArrayList<EquipmentDetails> getApprovedList() {
        return arrayList;
    }
}
