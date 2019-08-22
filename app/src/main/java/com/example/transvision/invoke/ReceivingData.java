package com.example.transvision.invoke;

import android.os.Handler;

import com.example.transvision.adapters.CheckboxAdapter;
import com.example.transvision.adapters.ManagementAdapter;
import com.example.transvision.adapters.ReceivedAdapter;
import com.example.transvision.adapters.ViewApprovalAdapter;
import com.example.transvision.model.EquipmentDetails;
import com.example.transvision.values.FunctionCall;
import com.example.transvision.values.GetSetValues;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.transvision.values.constants.EQUIPMENT_REQUEST_FAILURE;
import static com.example.transvision.values.constants.EQUIPMENT_REQUEST_SUCCESS;
import static com.example.transvision.values.constants.ITEM_FETCH_FAILURE;
import static com.example.transvision.values.constants.ITEM_FETCH_SUCCESS;
import static com.example.transvision.values.constants.LOGIN_FAILURE;
import static com.example.transvision.values.constants.LOGIN_SUCCESS;
import static com.example.transvision.values.constants.MANAGEMENT_APPROVAL_FAILURE;
import static com.example.transvision.values.constants.MANAGEMENT_APPROVAL_SUCCESS;
import static com.example.transvision.values.constants.RECEIVED_APPROVAL_FAILURE;
import static com.example.transvision.values.constants.RECEIVED_APPROVAL_SUCCESS;
import static com.example.transvision.values.constants.REQUEST_RESULT_FAILURE;
import static com.example.transvision.values.constants.REQUEST_RESULT_SUCCESS;

public class ReceivingData {

    private String parseServerXML(String result) {
        String value = "";
        XmlPullParserFactory pullParserFactory;
        InputStream res;
        try {
            res = new ByteArrayInputStream(result.getBytes());
            pullParserFactory = XmlPullParserFactory.newInstance();
            pullParserFactory.setNamespaceAware(true);
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(res, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("string".equals(name)) {
                            value = parser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            value = result;
        }
        return value;
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    void getItemResult(String result, android.os.Handler handler, ArrayList<GetSetValues> arrayList, GetSetValues getSetValues,
                       CheckboxAdapter checkboxAdapter) {
        result = parseServerXML(result);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    getSetValues = new GetSetValues();
                    String item_name = jsonObject.getString("NAMES");
                    getSetValues.setItem_name(item_name);
                    arrayList.add(getSetValues);
                    checkboxAdapter.notifyDataSetChanged();
                }
                handler.sendEmptyMessage(ITEM_FETCH_SUCCESS);
            } else handler.sendEmptyMessage(ITEM_FETCH_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(ITEM_FETCH_FAILURE);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------
    void getEquipmentResult(String result, Handler handler, GetSetValues getSetValues) {
        result = parseServerXML(result);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String message = jsonObject.getString("message");
            if (StringUtils.startsWithIgnoreCase(message, "Success")) {
                getSetValues.setId(jsonObject.getString("id"));
                handler.sendEmptyMessage(EQUIPMENT_REQUEST_SUCCESS);
            } else handler.sendEmptyMessage(EQUIPMENT_REQUEST_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(EQUIPMENT_REQUEST_FAILURE);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------
    void getMgmtAprrovalResult(String result, Handler handler) {
        result = parseServerXML(result);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String message = jsonObject.getString("message");
            if (StringUtils.startsWithIgnoreCase(message, "Success"))
                handler.sendEmptyMessage(MANAGEMENT_APPROVAL_SUCCESS);
            else handler.sendEmptyMessage(MANAGEMENT_APPROVAL_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(MANAGEMENT_APPROVAL_FAILURE);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------
    void getRequestResult(String result, Handler handler, ArrayList<EquipmentDetails> arrayList, ViewApprovalAdapter viewApprovalAdapter,
                          ManagementAdapter managementAdapter, boolean a, boolean b) {
        result = parseServerXML(result);
        arrayList.clear();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                EquipmentDetails equipmentDetails = new Gson().fromJson(jsonObject.toString(), EquipmentDetails.class);
                arrayList.add(equipmentDetails);
                if (a) {
                    viewApprovalAdapter.notifyDataSetChanged();
                } else if (b) {
                    managementAdapter.notifyDataSetChanged();
                }
            }
            if (arrayList.size() > 0)
                handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
            else handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------
    void getReceivedAprrovalResult(String result, Handler handler) {
        result = parseServerXML(result);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String message = jsonObject.getString("message");
            if (StringUtils.startsWithIgnoreCase(message, "Success"))
                handler.sendEmptyMessage(RECEIVED_APPROVAL_SUCCESS);
            else handler.sendEmptyMessage(RECEIVED_APPROVAL_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(RECEIVED_APPROVAL_FAILURE);
        }
    }

    //******************************FOR getting result based on user LOGIN*****************************************************
    void get_Details(String result, GetSetValues getSetValues, Handler handler) {
        result = parseServerXML(result);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String message = jsonObject.getString("message");
            if (StringUtils.startsWithIgnoreCase(message, "Success")) {
                getSetValues.setUser_name(jsonObject.getString("USERNAME"));
                getSetValues.setSubdiv_code(jsonObject.getString("SUBDIVCODE"));
                getSetValues.setUser_role(jsonObject.getString("GROUPS"));
                handler.sendEmptyMessage(LOGIN_SUCCESS);
            } else handler.sendEmptyMessage(LOGIN_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(LOGIN_FAILURE);
        }
    }

    //------------------------------------------------------------------------------------------------------------------------------------
    void getReceivedRequestResult(String result, Handler handler, ArrayList<EquipmentDetails> arrayList, ReceivedAdapter departmentAdapter) {
        result = parseServerXML(result);
        arrayList.clear();
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                EquipmentDetails equipmentDetails = new Gson().fromJson(jsonObject.toString(), EquipmentDetails.class);
                arrayList.add(equipmentDetails);
                departmentAdapter.notifyDataSetChanged();
            }
            if (arrayList.size() > 0)
                handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
            else handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
        }
    }

}
