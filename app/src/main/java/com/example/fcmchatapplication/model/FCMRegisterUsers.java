package com.example.fcmchatapplication.model;

import java.util.ArrayList;
import java.util.List;

public class FCMRegisterUsers {
    private String fcmUserId;
    private String userName;
    private String userEmail;
    private String deviceToken;
    private double latitude;
    private double longitude;
    private String createDate;
    private String profileURL;
    private List<String> subscribeList= new ArrayList<>();


    public FCMRegisterUsers(){

    }

    public FCMRegisterUsers(String fcmUserId, String userName, String userEmail,
                            String deviceToken, double latitude, double longitude, String createDate, String profileURL,List<String> subscribeList) {
        this.fcmUserId = fcmUserId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.deviceToken = deviceToken;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createDate = createDate;
        this.profileURL = profileURL;
        this.subscribeList=subscribeList;
    }

    public String getFcmUserId() {
        return fcmUserId;
    }

    public void setFcmUserId(String fcmUserId) {
        this.fcmUserId = fcmUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }





    public List<String> getSubscribeList() {
        return subscribeList;
    }

    public void setSubscribeList(List<String> subscribeList) {
        this.subscribeList = subscribeList;
    }







}
