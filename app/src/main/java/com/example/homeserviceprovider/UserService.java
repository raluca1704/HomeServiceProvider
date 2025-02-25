package com.example.homeserviceprovider;

public class UserService {
    private String serviceName;
    private String description;
    private String location;
    private String price;
    private String userId;

    public UserService(String serviceName, String description, String location, String price, String userId) {
        this.serviceName = serviceName;
        this.description = description;
        this.location = location;
        this.price = price;
        this.userId= userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }
}
