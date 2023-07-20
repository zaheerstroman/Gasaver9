package com.gasaver.Response;

import com.gasaver.model.CategoryModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetUpdatePriceingResponse extends BaseResponse {

    @SerializedName("data")
    ArrayList<GetUpdatePriceingModel> data;

    public ArrayList<GetUpdatePriceingModel> getData() {
        return data;
    }

    public void setData(ArrayList<GetUpdatePriceingModel> data) {
        this.data = data;
    }

    public class GetUpdatePriceingModel{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("lastupdated")
    @Expose
    private String lastupdated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    }
}
