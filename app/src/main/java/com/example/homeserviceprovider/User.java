package com.example.homeserviceprovider;

import java.util.List;
import java.util.Map;

public class User {
    private String id; // ID-ul utilizatorului
    private String name;
    private String email;
    private String address;
    private List<Map<String, Object>> reviews; // Lista de Map pentru recenzii

    // Constructor implicit (fără parametri) pentru Firestore
    public User() {
        // Constructor implicit necesar pentru Firestore
    }

    // Constructor care include id-ul utilizatorului
    public User(String id, String name, String email, String address, List<Map<String, Object>> reviews) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.address = address;
        this.reviews = reviews;
    }

    // Getter și Setter pentru id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getter și Setter pentru celelalte câmpuri
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Map<String, Object>> getReviews() {
        return reviews;
    }

    public void setReviews(List<Map<String, Object>> reviews) {
        this.reviews = reviews;
    }
}
