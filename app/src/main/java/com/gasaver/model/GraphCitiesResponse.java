package com.gasaver.model;

import com.gasaver.Response.BaseResponseGasaver;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GraphCitiesResponse {

    @SerializedName("status_code")
    @Expose
    private Boolean statusCode;
    @SerializedName("data")
    @Expose
    private ArrayList<GraphCitiesModel> data;

    public ArrayList<GraphCitiesModel> getData() {
        return this.data;
    }

    public void setData(ArrayList<GraphCitiesModel> data) {
        this.data = data;
    }

}
