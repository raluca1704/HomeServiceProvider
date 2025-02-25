package com.example.homeserviceprovider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
public class UserListActivity extends navDrawer {

    private TextView serviceNameTextView;
    private RecyclerView userRecyclerView;
    private UserServiceAdapter userServiceAdapter;
    private List<UserService> userServiceList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Inițializăm elementele UI
        serviceNameTextView = findViewById(R.id.serviceNameTextView);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        // Inițializăm lista și adapter-ul
        userServiceList = new ArrayList<>();
        userServiceAdapter = new UserServiceAdapter(this, userServiceList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.setAdapter(userServiceAdapter);

        // Inițializăm Firestore
        db = FirebaseFirestore.getInstance();

        // Obținem datele trimise din activitatea precedentă
        String serviceName = getIntent().getStringExtra("serviceName");

        // Setăm titlul serviciului
        if (serviceName != null) {
            serviceNameTextView.setText(serviceName);
            loadUserServices(serviceName); // Începem încărcarea utilizatorilor care oferă acest serviciu
        } else {
            Toast.makeText(this, "Service name not provided.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserServices(String serviceName) {
        progressBar.setVisibility(View.VISIBLE);

        // Căutăm în colecția "userServices" utilizatorii care oferă serviciul specificat
        db.collection("userServices")
                .whereEqualTo("serviceName", serviceName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String description = document.getString("description");
                            String location = document.getString("location");
                            String price = document.getString("price");
                            String userId = document.getString("userId");

                            // Adăugăm serviciul în listă
                            UserService userService = new UserService(serviceName, description, location, price, userId);
                            userServiceList.add(userService);
                        }

                        // Actualizăm lista după încărcare
                        userServiceAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "No users found for this service.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Log.e("UserListActivity", "Error loading user services: " + e.getMessage());
                    Toast.makeText(this, "Error loading user services.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }
}
