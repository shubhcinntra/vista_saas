package com.cinntra.vista.model;

import java.util.ArrayList;

public class ResponseBusinessType {
    public String message;
    public String status;
    public ArrayList<DataBusinessType> data;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<DataBusinessType> getData() {
        return data;
    }

    public void setData(ArrayList<DataBusinessType> data) {
        this.data = data;
    }
}


