package com.example.transvision.invoke;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.example.transvision.adapters.CheckboxAdapter;
import com.example.transvision.adapters.ManagementAdapter;
import com.example.transvision.adapters.ReceivedAdapter;
import com.example.transvision.adapters.ViewApprovalAdapter;
import com.example.transvision.model.EquipmentDetails;
import com.example.transvision.values.GetSetValues;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.example.transvision.values.constants.HTTP_SERVER_TIME_OUT;

public class SendingData {
    private ReceivingData receivingData = new ReceivingData();

    //In below code try catch has been added to check the response timeout
    private String UrlPostConnection(String Post_Url, HashMap<String, String> datamap) throws IOException {
        try {
            StringBuilder response = new StringBuilder();
            URL url = new URL(Post_Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(HTTP_SERVER_TIME_OUT);
            conn.setConnectTimeout(HTTP_SERVER_TIME_OUT);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(getPostDataString(datamap));
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            } else {
                response = new StringBuilder();
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Debug", "SERVER TIME OUT");
        }
        return null;
    }

    private String UrlGetConnection(String Get_Url) throws IOException {
        String response;
        URL url = new URL(Get_Url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(HTTP_SERVER_TIME_OUT);
        conn.setConnectTimeout(HTTP_SERVER_TIME_OUT);
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder responseBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                responseBuilder.append(line);
            }
            response = responseBuilder.toString();
        } else response = "";
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public class GetItemNames extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;
        GetSetValues getSetValues;
        ArrayList<GetSetValues> arrayList;
        CheckboxAdapter checkboxAdapter;

        public GetItemNames(Handler handler, ArrayList<GetSetValues> arrayList, GetSetValues getSetValues, CheckboxAdapter checkboxAdapter) {
            this.handler = handler;
            this.arrayList = arrayList;
            this.getSetValues = getSetValues;
            this.checkboxAdapter = checkboxAdapter;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                response = UrlGetConnection("http://bc_service2.hescomtrm.com/TicketingService.asmx/Equipment_Names");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.getItemResult(result, handler, arrayList, getSetValues, checkboxAdapter);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public class Equipment_Request extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;
        GetSetValues getSetValues;

        public Equipment_Request(Handler handler, GetSetValues getSetValues) {
            this.handler = handler;
            this.getSetValues=getSetValues;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("USER_NAME", params[0]);
            datamap.put("MOBILE_NO", params[1]);
            datamap.put("SUBDIV_CODE", params[2]);
            datamap.put("ITEMS", params[3]);
            datamap.put("REMARK", params[4]);
            try {
                response = UrlPostConnection("http://bc_service2.hescomtrm.com/TicketingService.asmx/Equipment_Insert", datamap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.getEquipmentResult(result, handler,getSetValues);
            super.onPostExecute(result);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public class Management_Approval extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;

        public Management_Approval(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("id", params[0]);
            try {
                response = UrlPostConnection("http://bc_service2.hescomtrm.com/TicketingService.asmx/Mgmt_Approval", datamap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.getMgmtAprrovalResult(result, handler);
            super.onPostExecute(result);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public class Fetch_Requests extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;
        ArrayList<EquipmentDetails> arrayList;
        ViewApprovalAdapter viewApprovalAdapter;
        ManagementAdapter managementAdapter;
        boolean a, b;

        public Fetch_Requests(Handler handler, ArrayList<EquipmentDetails> arrayList,
                              ViewApprovalAdapter viewApprovalAdapter, ManagementAdapter managementAdapter, boolean a, boolean b) {
            this.handler = handler;
            this.arrayList = arrayList;
            this.viewApprovalAdapter = viewApprovalAdapter;
            this.managementAdapter = managementAdapter;
            this.a = a;
            this.b = b;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                response = UrlGetConnection("http://bc_service2.hescomtrm.com/TicketingService.asmx/Equipment_Details");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.getRequestResult(result, handler, arrayList, viewApprovalAdapter, managementAdapter, a, b);
            super.onPostExecute(result);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public class Received_Approval extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;

        public Received_Approval(Handler handler) {
            this.handler = handler;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("Id", params[0]);
            datamap.put("FileName", params[1]);
            datamap.put("EnodedFile", params[2]);
            try {
                response = UrlPostConnection("http://bc_service2.hescomtrm.com/TicketingService.asmx/Received_Approval", datamap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.getReceivedAprrovalResult(result, handler);
            super.onPostExecute(result);
        }
    }

    //**********************************************For user Login***********************************************************************
    @SuppressLint("StaticFieldLeak")
    public class Login extends AsyncTask<String, String, String> {
        String response = "";
        GetSetValues getSetValues;
        Handler handler;

        public Login(GetSetValues getSetValues, Handler handler) {
            this.getSetValues = getSetValues;
            this.handler = handler;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("username", params[0]);
            datamap.put("password", params[1]);
            try {
                response = UrlPostConnection("http://bc_service2.hescomtrm.com/TicketingService.asmx/loginDetails", datamap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            receivingData.get_Details(result, getSetValues, handler);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public class Fetch_Received_Requests extends AsyncTask<String, String, String> {
        String response = "";
        Handler handler;
        ArrayList<EquipmentDetails> arrayList;
        ReceivedAdapter departmentAdapter;

        public Fetch_Received_Requests(Handler handler, ArrayList<EquipmentDetails> arrayList, ReceivedAdapter departmentAdapter) {
            this.handler = handler;
            this.arrayList = arrayList;
            this.departmentAdapter = departmentAdapter;
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> datamap = new HashMap<>();
            datamap.put("SUBDIV_CODE", params[0]);

            try {
                response = UrlPostConnection("http://bc_service2.hescomtrm.com/TicketingService.asmx/View_received_approval", datamap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            receivingData.getReceivedRequestResult(result, handler, arrayList, departmentAdapter);
            super.onPostExecute(result);
        }
    }

}
