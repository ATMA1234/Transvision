package com.example.transvision.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.transvision.R;
import com.example.transvision.adapters.CheckboxAdapter;
import com.example.transvision.invoke.SendingData;
import com.example.transvision.values.FunctionCall;
import com.example.transvision.values.GetSetValues;

import java.util.ArrayList;

import static com.example.transvision.values.constants.EQUIPMENT_REQUEST_FAILURE;
import static com.example.transvision.values.constants.EQUIPMENT_REQUEST_SUCCESS;
import static com.example.transvision.values.constants.ITEM_FETCH_FAILURE;
import static com.example.transvision.values.constants.ITEM_FETCH_SUCCESS;

public class RequestEquipments extends AppCompatActivity implements View.OnClickListener {
    private static final int DIALOG_REQUEST_SUCCESS = 1;
    FunctionCall functionCall;
    Toolbar toolbar;
    Spinner spinner;
    ArrayList<GetSetValues> arrayList;
    CheckboxAdapter checkboxAdapter;
    SendingData sendingData;
    GetSetValues getSetValues;
    EditText item_names, mob_no, remark;
    boolean checkbox_selected = false;
    String item_list = "";
    StringBuilder stringBuilder;
    Button submit;
    ProgressDialog progressDialog;
    TextView items_done, user_name, subdiv_code;
    String USER_NAME = "", SUBDIV_CODE = "";

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case ITEM_FETCH_SUCCESS:
                    break;

                case ITEM_FETCH_FAILURE:
                    finish();
                    break;

                case EQUIPMENT_REQUEST_SUCCESS:
                    progressDialog.dismiss();
                    showdialog(DIALOG_REQUEST_SUCCESS);
                    break;

                case EQUIPMENT_REQUEST_FAILURE:
                    progressDialog.dismiss();
                    functionCall.showToast(RequestEquipments.this,"Failure");
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_equipments);
        initialize();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_selected:
                getItemNames();
                break;

            case R.id.btn_submit:
                validateSubmission();
                break;
        }
    }

    public void initialize() {
        Intent intent = getIntent();
        USER_NAME = intent.getStringExtra("user_name");
        SUBDIV_CODE = intent.getStringExtra("subdiv_code");
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Request Equipments");
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSetValues=new GetSetValues();
        progressDialog = new ProgressDialog(this);
        spinner = findViewById(R.id.spinner);
        user_name = findViewById(R.id.txt_user_name);
        user_name.setText(USER_NAME);
        subdiv_code = findViewById(R.id.txt_subdiv_code);
        subdiv_code.setText(SUBDIV_CODE);
        mob_no = findViewById(R.id.et_mob_nbr);
        remark = findViewById(R.id.et_remark);
        item_names = findViewById(R.id.et_item_names);
        functionCall = new FunctionCall();
        submit = findViewById(R.id.btn_submit);
        submit.setOnClickListener(this);
        items_done = findViewById(R.id.txt_selected);
        items_done.setOnClickListener(this);
        arrayList = new ArrayList<>();
        checkboxAdapter = new CheckboxAdapter(arrayList, getApplicationContext());
        spinner.setAdapter(checkboxAdapter);
        sendingData = new SendingData();

        SendingData.GetItemNames sendSubdivCodeRequest = sendingData.new GetItemNames(handler, arrayList, getSetValues,
                checkboxAdapter);
        sendSubdivCodeRequest.execute();
    }

    public void getItemNames() {
        ArrayList<GetSetValues> approvedlist = checkboxAdapter.getApprovedList();
        for (int j = 0; j < approvedlist.size(); j++) {
            GetSetValues getSetValues1 = approvedlist.get(j);
            if (getSetValues1.isSelected()) {
                checkbox_selected = true;
            }
        }
        if (checkbox_selected) {
            stringBuilder = new StringBuilder();
            for (int i = 0; i < approvedlist.size(); i++) {
                GetSetValues getSetValues = approvedlist.get(i);
                if (getSetValues.isSelected()) {
                    stringBuilder.append(getSetValues.getItem_name()).append(",");
                }
            }
            item_list = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
            item_names.setText(item_list);
        }
    }

    public void validateSubmission() {
        if (TextUtils.isEmpty(mob_no.getText())) {
            functionCall.showToast(this, "Please Enter Mobile Number");
            return;
        }
        if (mob_no.getText().length() < 10) {
            functionCall.showToast(this, "Please Enter Correct Mobile Number");
            return;
        }
        if (TextUtils.isEmpty(item_names.getText())) {
            functionCall.showToast(this, "Please select items");
            return;
        }
        functionCall.showprogressdialog("Please wait to complete...", progressDialog, "Inserting records");
        SendingData.Equipment_Request equipment_request = sendingData.new Equipment_Request(handler,getSetValues);
        equipment_request.execute(USER_NAME, mob_no.getText().toString(), SUBDIV_CODE,
                item_names.getText().toString(), remark.getText().toString());
    }

    //------------------------------------Method for alert dialog---------------------------------------------------------------//
    private void showdialog(int id) {
        final AlertDialog alertDialog;
        if (id == DIALOG_REQUEST_SUCCESS) {
            AlertDialog.Builder request_dialog = new AlertDialog.Builder(this);
            request_dialog.setTitle("Item Request");
            request_dialog.setMessage("Your request has been submitted & your request id:"+getSetValues.getId());
            request_dialog.setCancelable(false);
            request_dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            alertDialog = request_dialog.create();
            alertDialog.show();
        }
    }
}
