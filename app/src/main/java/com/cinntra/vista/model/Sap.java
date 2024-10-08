package com.cinntra.vista.model;


import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sap implements Parcelable
{

    @SerializedName("CompanyDB")
    @Expose
    private String companyDB;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("UserName")
    @Expose
    private String userName;
    public final static Creator<Sap> CREATOR = new Creator<Sap>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Sap createFromParcel(android.os.Parcel in) {
            return new Sap(in);
        }

        public Sap[] newArray(int size) {
            return (new Sap[size]);
        }

    }
            ;

    protected Sap(android.os.Parcel in) {
        this.companyDB = ((String) in.readValue((String.class.getClassLoader())));
        this.password = ((String) in.readValue((String.class.getClassLoader())));
        this.userName = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Sap() {
    }

    /**
     *
     * @param companyDB
     * @param password
     * @param userName
     */
    public Sap(String companyDB, String password, String userName) {
        super();
        this.companyDB = companyDB;
        this.password = password;
        this.userName = userName;
    }

    public String getCompanyDB() {
        return companyDB;
    }

    public void setCompanyDB(String companyDB) {
        this.companyDB = companyDB;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(companyDB);
        dest.writeValue(password);
        dest.writeValue(userName);
    }

    public int describeContents() {
        return 0;
    }

}