package com.cinntra.vista.model;

import java.util.ArrayList;

public class ResponseZoneDropDown {
    public String message;
    public String status;
    public ArrayList<DataDropDownZone> data;

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

    public ArrayList<DataDropDownZone> getData() {
        return data;
    }

    public void setData(ArrayList<DataDropDownZone> data) {
        this.data = data;
    }
}
