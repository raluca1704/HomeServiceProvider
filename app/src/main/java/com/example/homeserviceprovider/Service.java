package com.example.homeserviceprovider;

import java.util.List;

public class Service {
    private String serviceName;
    private List<String> users; // List of user IDs

    // Constructor
    public Service(String serviceName, List<String> users) {
        this.serviceName = serviceName;
        this.users = users;
    }

    // Getter și setter pentru serviceName
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    // Getter și setter pentru users
    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
