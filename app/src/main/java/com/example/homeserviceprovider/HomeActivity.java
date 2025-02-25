package com.example.homeserviceprovider;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceAdapter serviceAdapter;
    private List<Service> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeActivity", "onCreate started");

        try {
            setContentView(R.layout.activity_home);
            Log.d("HomeActivity", "Layout set successfully");

            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            if (recyclerView == null) {
                Log.e("HomeActivity", "RecyclerView not found!");
                return;
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            Log.d("HomeActivity", "RecyclerView configured");
//
//            // Inițializează lista de servicii
//            List<Service> serviceList = new ArrayList<>();
//            serviceList.add(new Service("Plumbing", R.drawable.ic_plumbing));
//            serviceList.add(new Service("Electrical", R.drawable.ic_electrical));
//            serviceList.add(new Service("Babysitting", R.drawable.ic_babysitting));
//            serviceList.add(new Service("Cleaning", R.drawable.ic_cleaning));
//            Log.d("HomeActivity", "Service list initialized with " + serviceList.size() + " items");
//
//            // Configurează adapterul
            recyclerView.setAdapter(serviceAdapter);
            Log.d("HomeActivity", "Adapter set successfully");

        } catch (Exception e) {
            Log.e("HomeActivity", "Error in onCreate: " + e.getMessage(), e);
        }
    }}