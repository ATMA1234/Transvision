package com.example.transvision.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.transvision.R;
import com.example.transvision.activities.ReceivedApproval;
import com.example.transvision.model.EquipmentDetails;

import java.util.ArrayList;

import static com.example.transvision.values.constants.DLG_RECEIVED_SUBMIT;

public class ReceivedAdapter extends RecyclerView.Adapter<ReceivedAdapter.Approvals_ViewHolder> {
    private Context context;
    private ArrayList<EquipmentDetails> arrayList;

    public ReceivedAdapter(Context context, ArrayList<EquipmentDetails> arrayList) {
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
        if ((arrayList.get(i).getRECEIVED_FLAG()).equals("Y")) {
            approvals_viewHolder.main_layout.setVisibility(View.GONE);
        } else {
            approvals_viewHolder.tv_id.setText(equipmentDetails.getID());
            approvals_viewHolder.tv_user_name.setText(equipmentDetails.getUSER_NAME());
            approvals_viewHolder.tv_sudiv_code.setText(equipmentDetails.getSUBDIV_CODE());
            approvals_viewHolder.tv_equipments.setText(equipmentDetails.getITEMS());
            approvals_viewHolder.tv_approved_date.setText(equipmentDetails.getAPPROVED_DATE());
        }
        approvals_viewHolder.management.setVisibility(View.GONE);
        approvals_viewHolder.remark.setVisibility(View.GONE);
        approvals_viewHolder.received.setVisibility(View.GONE);
        approvals_viewHolder.request.setVisibility(View.GONE);
        approvals_viewHolder.mob_no.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    class Approvals_ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_id, tv_user_name, tv_equipments, tv_sudiv_code, tv_approved_date;
        Button button;
        LinearLayout main_layout, management, remark, received, request, mob_no;

        Approvals_ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_id = itemView.findViewById(R.id.txt_id);
            tv_user_name = itemView.findViewById(R.id.txt_user_name);
            tv_equipments = itemView.findViewById(R.id.txt_equipments);
            tv_sudiv_code = itemView.findViewById(R.id.txt_subdiv_code);
            tv_approved_date = itemView.findViewById(R.id.txt_approved_date);
            button = itemView.findViewById(R.id.btn_submit);
            button.setOnClickListener(this);
            main_layout = itemView.findViewById(R.id.lin_main_layout);
            management = itemView.findViewById(R.id.lin_management);
            remark = itemView.findViewById(R.id.lin_remark);
            received = itemView.findViewById(R.id.lin_received_date);
            request = itemView.findViewById(R.id.lin_req_date);
            mob_no = itemView.findViewById(R.id.lin_mob_no);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (v.getId() == R.id.btn_submit) {
                ((ReceivedApproval) context).showdialog(DLG_RECEIVED_SUBMIT, pos, arrayList);
            }
        }
    }
}
