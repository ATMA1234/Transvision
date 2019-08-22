package com.example.transvision;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transvision.activities.Attendance;
import com.example.transvision.activities.ManagementApproval;
import com.example.transvision.activities.ReceivedApproval;
import com.example.transvision.activities.RequestEquipments;
import com.example.transvision.activities.ViewAllRequests;
import com.example.transvision.activities.ViewAllRequests1;
import com.example.transvision.values.FunctionCall;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    Button equipment, management, received, approvals, attendance;
    String USER_NAME = "", SUBDIV_CODE = "", USER_ROLE = "";
    FunctionCall functionCall;
    private boolean isFirstBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView user_name = header.findViewById(R.id.nav_user_name);
        TextView subdiv_code = header.findViewById(R.id.nav_subdiv_code);
        TextView tv_close = header.findViewById(R.id.nav_ic_close);
        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        user_name.setText(USER_NAME);
        subdiv_code.setText(SUBDIV_CODE);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.black));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Menu nav_Menu = navigationView.getMenu();
        if (USER_ROLE.equals("Management")) {
            nav_Menu.findItem(R.id.nav_management).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_management).setVisible(false);
        }

        //*****************************set app version to drawer******************************************************************************
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String main_curr_version = null;
        if (pInfo != null) {
            main_curr_version = pInfo.versionName;
        }

        NavigationView logout_navigationView = findViewById(R.id.nav_drawer_bottom);
        logout_navigationView.setNavigationItemSelectedListener(this);
        logout_navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black)));
        Menu menu = logout_navigationView.getMenu();
        MenuItem nav_login = menu.findItem(R.id.nav_version);
        nav_login.setTitle("Version" + " : " + main_curr_version);
        nav_login.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
            super.onBackPressed();
        } else {
            if (isFirstBackPressed) {
                super.onBackPressed();
            } else {
                isFirstBackPressed = true;
                Toast.makeText(this, "Press again to close app", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFirstBackPressed = false;
                    }
                }, 2000);
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (functionCall.isInternetOn(Objects.requireNonNull(this))) {
            Intent intent = null;
            if (id == R.id.nav_request) {
                intent = new Intent(MainActivity.this, RequestEquipments.class);
            } else if (id == R.id.nav_management) {
                intent = new Intent(MainActivity.this, ManagementApproval.class);
            } else if (id == R.id.nav_received) {
                intent = new Intent(MainActivity.this, ReceivedApproval.class);
                intent.putExtra("subdiv_code", SUBDIV_CODE);
            } else if (id == R.id.nav_approvals) {
                intent = new Intent(MainActivity.this, ViewAllRequests.class);
            } else if (id == R.id.nav_attendance) {
                intent = new Intent(MainActivity.this, Attendance.class);
            } else if (id == R.id.nav_logout) {
                intent = new Intent(MainActivity.this, LoginActivity.class);
                finish();
            }
            startActivity(intent);
        } else snackBar();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //--------------------------------------------------------------------------------------------------------------------------
    public void initialize() {
        functionCall = new FunctionCall();
        Intent intent = getIntent();
        USER_NAME = intent.getStringExtra("user_name");
        SUBDIV_CODE = intent.getStringExtra("subdiv_code");
        USER_ROLE = intent.getStringExtra("user_role");
        equipment = findViewById(R.id.btn_req_equipment);
        equipment.setOnClickListener(this);
        management = findViewById(R.id.btn_mgmnt_approval);
        management.setOnClickListener(this);
        received = findViewById(R.id.btn_received_approval);
        received.setOnClickListener(this);
        approvals = findViewById(R.id.btn_view_approvals);
        approvals.setOnClickListener(this);
        attendance = findViewById(R.id.btn_attendance);
        attendance.setOnClickListener(this);
        if (USER_ROLE.equals("Management")) {
            management.setVisibility(View.VISIBLE);
        } else {
            management.setVisibility(View.GONE);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_req_equipment:
                if (functionCall.isInternetOn(this)) {
                    Intent intent = new Intent(MainActivity.this, RequestEquipments.class);
                    intent.putExtra("user_name", USER_NAME);
                    intent.putExtra("subdiv_code", SUBDIV_CODE);
                    startActivity(intent);
                } else snackBar();
                break;
            case R.id.btn_mgmnt_approval:
                if (functionCall.isInternetOn(this)) {
                    Intent intent = new Intent(MainActivity.this, ManagementApproval.class);
                    startActivity(intent);
                } else snackBar();
                break;
            case R.id.btn_received_approval:
                if (functionCall.isInternetOn(this)) {
                    Intent intent = new Intent(MainActivity.this, ReceivedApproval.class);
                    intent.putExtra("subdiv_code", SUBDIV_CODE);
                    startActivity(intent);
                } else snackBar();
                break;
            case R.id.btn_view_approvals:
                if (functionCall.isInternetOn(this)) {
                    Intent intent = new Intent(MainActivity.this, ViewAllRequests1.class);
                    startActivity(intent);
                } else snackBar();
                break;
            case R.id.btn_attendance:
                if (functionCall.isInternetOn(this)) {
                    Intent intent = new Intent(MainActivity.this, Attendance.class);
                    startActivity(intent);
                } else snackBar();
                break;
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------
    public void snackBar() {
        final DrawerLayout linearLayout = findViewById(R.id.drawer_layout);
        Snackbar snackbar = Snackbar.make(linearLayout, "Please turn on internet & proceed.", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
