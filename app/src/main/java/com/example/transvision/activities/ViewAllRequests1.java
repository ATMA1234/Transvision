package com.example.transvision.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.transvision.R;
import com.example.transvision.adapters.ViewApprovalAdapter;
import com.example.transvision.api.RegisterAPI;
import com.example.transvision.api.RetroClient;
import com.example.transvision.invoke.SendingData;
import com.example.transvision.model.EquipmentDetails;
import com.example.transvision.values.FunctionCall;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.transvision.values.constants.REQUEST_RESULT_FAILURE;
import static com.example.transvision.values.constants.REQUEST_RESULT_SUCCESS;

public class ViewAllRequests1 extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    RecyclerView view_all_approvals;
    ViewApprovalAdapter viewApprovalAdapter;
    List<EquipmentDetails> equipmentDetails;
    SendingData sendingData;
    FunctionCall functionCall;
    ProgressDialog progressDialog;
    FloatingActionButton btn_new_request;

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
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_requests);
        initialize();
    }

    //----------------------------------------------------------------------------------------------------------------------------
    public void initialize() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setTitle("View All Requests");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendingData = new SendingData();
        functionCall = new FunctionCall();
        progressDialog = new ProgressDialog(this);
        btn_new_request = findViewById(R.id.fab_new_request);
        btn_new_request.setOnClickListener(this);

        view_all_approvals = findViewById(R.id.approval_recycler);
        equipmentDetails = new ArrayList<>();
        viewApprovalAdapter = new ViewApprovalAdapter(this, equipmentDetails);
        view_all_approvals.setHasFixedSize(true);
        view_all_approvals.setLayoutManager(new LinearLayoutManager(this));
        view_all_approvals.setAdapter(viewApprovalAdapter);

        functionCall.showprogressdialog("Please wait to complete....", progressDialog, "Fetching requests");
        EquipmentData();
    }

    //Request or post data **********************************************************************************************************
    public void EquipmentData() {
        RegisterAPI api = RetroClient.getApiService();
        api.getData().enqueue(new Callback<List<EquipmentDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<EquipmentDetails>> call, @NonNull Response<List<EquipmentDetails>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    equipmentDetails = response.body();
                    view_all_approvals = findViewById(R.id.approval_recycler);
                    viewApprovalAdapter = new ViewApprovalAdapter(getApplicationContext(), equipmentDetails);
                    view_all_approvals.setHasFixedSize(true);
                    view_all_approvals.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    view_all_approvals.setAdapter(viewApprovalAdapter);
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<EquipmentDetails>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }


    //*************search record in a list*********************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Enter Request ID");
        searchView.setBackgroundColor(Color.WHITE);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //*******************************************************************************************************
    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewApprovalAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab_new_request) {
            Intent intent = new Intent(ViewAllRequests1.this, RequestEquipments.class);
            startActivity(intent);
        }
    }
}
