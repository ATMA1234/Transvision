package com.example.transvision;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.transvision.invoke.SendingData;
import com.example.transvision.values.FunctionCall;
import com.example.transvision.values.GetSetValues;


import static com.example.transvision.values.constants.LOGIN_FAILURE;
import static com.example.transvision.values.constants.LOGIN_SUCCESS;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button login;
    EditText userName, password;
    SendingData sendingData;
    GetSetValues getSetValues;
    FunctionCall functionCall;
    ProgressDialog progressDialog;

    @SuppressLint("SetTextI18n")
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case LOGIN_SUCCESS:
                    progressDialog.dismiss();
                    finish();
                    functionCall.showToast(LoginActivity.this, "Login Success");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("user_name", getSetValues.getUser_name());
                    intent.putExtra("subdiv_code", getSetValues.getSubdiv_code());
                    intent.putExtra("user_role", getSetValues.getUser_role());
                    startActivity(intent);
                    break;

                case LOGIN_FAILURE:
                    progressDialog.dismiss();
                    functionCall.showToast(LoginActivity.this, "Login Failure");
                    break;

            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_login);
        initialize();
    }

    //--------------------------------------------------------------------------------------------
    public void initialize() {
        progressDialog = new ProgressDialog(this);
        functionCall = new FunctionCall();
        getSetValues = new GetSetValues();
        sendingData = new SendingData();
        userName = findViewById(R.id.et_user_name);
        userName.setText("");
        password = findViewById(R.id.et_password);
        password.setText("");
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            if (functionCall.isInternetOn(this)) {
                userLogin();
            } else snackBar();
        }
    }

    //--------------------------------------------------------------------------------------------
    public void userLogin() {
        if (TextUtils.isEmpty(userName.getText())) {
            userName.setError("Please Enter UserName");
            return;
        }
        if (TextUtils.isEmpty(password.getText())) {
            password.setError("Please Enter Password");
            return;
        }
        functionCall.showprogressdialog("Please wait to complete...", progressDialog, "Login");
        SendingData.Login login = sendingData.new Login(getSetValues, handler);
        login.execute(userName.getText().toString(), password.getText().toString());
    }

    //--------------------------------------------------------------------------------------------
    public void snackBar() {
        final LinearLayout linearLayout = findViewById(R.id.lin_login);
        Snackbar snackbar = Snackbar.make(linearLayout, "Please turn on internet & proceed.", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
