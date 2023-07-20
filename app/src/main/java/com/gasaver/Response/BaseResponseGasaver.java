package com.gasaver.Response;

import com.google.gson.annotations.SerializedName;

public class BaseResponseGasaver {

    @SerializedName("status_code")
    Integer statusCode;

    @SerializedName("message")
    String message;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
