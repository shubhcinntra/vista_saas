package com.cinntra.vista.newapimodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GlobalResponse
{

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Integer status;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}