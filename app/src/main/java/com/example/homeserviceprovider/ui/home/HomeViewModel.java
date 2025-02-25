package com.example.homeserviceprovider.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homeserviceprovider.Service;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Service>> serviceList;
    private final FirebaseFirestore db;

    public HomeViewModel() {
        serviceList = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        loadAllServices(); // Load all services initially
    }

    // Method to load all services from Firestore
    private void loadAllServices() {
        db.collection("services")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Service> services = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String serviceName = document.getString("serviceName");
                        List<String> users = (List<String>) document.get("users");  // Get the users array
                        services.add(new Service(serviceName, users)); // Add service with name and users
                    }
                    serviceList.setValue(services); // Update the list with loaded services
                })
                .addOnFailureListener(e -> {
                    serviceList.setValue(new ArrayList<>()); // In case of failure, set an empty list
                });
    }

    // Method to search services based on the query (ignoring last 3 characters)
    public void searchServices(String query) {
        if (query.isEmpty()) {
            loadAllServices(); // If the query is empty, reload all services
        } else {
            final String normalizedQuery = query.trim().toLowerCase(); // Make it final

            final String searchQuery;
            if (normalizedQuery.length() > 3) {
                searchQuery = normalizedQuery.substring(0, normalizedQuery.length() - 3);
            } else {
                searchQuery = normalizedQuery; // If the length is <= 3, just use the full query
            }

            db.collection("services")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        List<Service> filteredServices = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String serviceName = document.getString("serviceName");

                            if (serviceName != null && serviceName.trim().toLowerCase().startsWith(searchQuery)) {
                                List<String> users = (List<String>) document.get("users");
                                filteredServices.add(new Service(serviceName, users)); // Add filtered service with name and users
                            }
                        }

                        if (filteredServices.isEmpty()) {
                            serviceList.setValue(new ArrayList<>());
                        } else {
                            serviceList.setValue(filteredServices);
                        }
                    })
                    .addOnFailureListener(e -> {
                        serviceList.setValue(new ArrayList<>()); // Show an empty list if there was an error
                    });
        }
    }

    public LiveData<List<Service>> getServiceList() {
        return serviceList;
    }
}
