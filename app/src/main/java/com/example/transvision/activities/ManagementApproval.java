package com.example.transvision.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.transvision.R;
import com.example.transvision.adapters.ManagementAdapter;
import com.example.transvision.adapters.ReceivedAdapter;
import com.example.transvision.adapters.ViewApprovalAdapter;
import com.example.transvision.invoke.SendingData;
import com.example.transvision.model.EquipmentDetails;
import com.example.transvision.values.FunctionCall;

import java.util.ArrayList;

import static com.example.transvision.values.constants.MANAGEMENT_APPROVAL_FAILURE;
import static com.example.transvision.values.constants.MANAGEMENT_APPROVAL_SUCCESS;
import static com.example.transvision.values.constants.REQUEST_RESULT_FAILURE;
import static com.example.transvision.values.constants.REQUEST_RESULT_SUCCESS;

public class ManagementApproval extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    RecyclerView view_all_approvals;
    ManagementAdapter managementAdapter;
    ArrayList<EquipmentDetails> equipmentDetails;
    SendingData sendingData;
    FunctionCall functionCall;
    ProgressDialog progressDialog;
    ViewApprovalAdapter viewApprovalAdapter;
    ReceivedAdapter departmentAdapter;
    boolean checkbox_selected = false;
    Button approval_submit;
    String requset_id = "";
    StringBuilder stringBuilder;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_RESULT_SUCCESS:
                    progressDialog.dismiss();
                    break;

                case REQUEST_RESULT_FAILURE:
                    progressDialog.dismiss();
                    finish();
                    break;

                case MANAGEMENT_APPROVAL_SUCCESS:
                    progressDialog.dismiss();
                    functionCall.showToast(ManagementApproval.this,"Success");
                    checkbox_selected = false;
                    startActivity(getIntent());
                    finish();
                    break;

                case MANAGEMENT_APPROVAL_FAILURE:
                    progressDialog.dismiss();
                    functionCall.showToast(ManagementApproval.this,"Failure");
                    checkbox_selected = false;
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management_approval);
        checkbox_selected = false;
        initialize();
    }

    public void initialize() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle("Management Approvals");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendingData = new SendingData();
        functionCall = new FunctionCall();
        progressDialog = new ProgressDialog(this);
        viewApprovalAdapter = new ViewApprovalAdapter(this, equipmentDetails);
        departmentAdapter = new ReceivedAdapter(this, equipmentDetails);
        approval_submit = findViewById(R.id.btn_submit_approval);
        approval_submit.setOnClickListener(this);

        view_all_approvals = findViewById(R.id.approval_recycler);
        equipmentDetails = new ArrayList<>();
        managementAdapter = new ManagementAdapter(this, equipmentDetails);
        view_all_approvals.setHasFixedSize(true);
        view_all_approvals.setLayoutManager(new LinearLayoutManager(this));
        view_all_approvals.setAdapter(managementAdapter);

        functionCall.showprogressdialog("Fetching approvals....Please wait....", progressDialog, "View All Approvals");
        SendingData.Fetch_Requests fetch_requests = sendingData.new Fetch_Requests(handler, equipmentDetails, viewApprovalAdapter,
                managementAdapter,  false, true);
        fetch_requests.execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit_approval) {
            postdata();
        }
    }

    private void postdata() {
        ArrayList<EquipmentDetails> approvedlist = managementAdapter.getApprovedList();
        for (int j = 0; j < approvedlist.size(); j++) {
            EquipmentDetails equipmentDetails1 = approvedlist.get(j);
            if (equipmentDetails1.isSelected()) {
                checkbox_selected = true;
            }
        }
        if (checkbox_selected) {
            stringBuilder = new StringBuilder();
            for (int i = 0; i < approvedlist.size(); i++) {
                EquipmentDetails equipmentDetails = approvedlist.get(i);
                if (equipmentDetails.isSelected()) {
                    stringBuilder.append(equipmentDetails.getID()).append(",");
                }
            }
            requset_id = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
            functionCall.showprogressdialog("Please wait to complete", progressDialog, "Updating");
            SendingData.Management_Approval management_approval = sendingData.new Management_Approval(handler);
            management_approval.execute(requset_id);
        } else
            functionCall.showToast(this, "Please click on checkbox & proceed");
    }
}
