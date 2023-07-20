package com.gasaver.Response;

import com.gasaver.model.CategoryModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CatResponse extends BaseResponse{

    @SerializedName("data")
    ArrayList<CategoryModel> data;

    public ArrayList<CategoryModel> getData() {
        return data;
    }

    public void setData(ArrayList<CategoryModel> data) {
        this.data = data;
    }


}
