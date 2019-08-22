package com.example.transvision.api;


import com.example.transvision.model.EquipmentDetails;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


public interface RegisterAPI {
    @GET("Equipment_Details")
    Call<List<EquipmentDetails>> getData();
}