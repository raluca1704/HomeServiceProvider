package com.example.homeserviceprovider.ui.createnewservice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homeserviceprovider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateNewServiceActivity extends AppCompatActivity {

    private RadioGroup radioGroupServiceType;
    private RadioButton radioCreateNewService, radioSelectExistingService;
    private EditText inputDescription, inputLocation, inputPrice, inputNewServiceName;
    private Spinner spinnerServices;
    private Button buttonSaveService;
    private FirebaseFirestore db;
    private List<String> servicesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_service);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        radioGroupServiceType = findViewById(R.id.radio_group_service_type);
        radioCreateNewService = findViewById(R.id.radio_create_new_service);
        radioSelectExistingService = findViewById(R.id.radio_select_existing_service);
        inputDescription = findViewById(R.id.input_description);
        inputLocation = findViewById(R.id.input_location);
        inputPrice = findViewById(R.id.input_price);
        inputNewServiceName = findViewById(R.id.input_new_service_name);
        spinnerServices = findViewById(R.id.spinner_services);
        buttonSaveService = findViewById(R.id.button_save_service);

        // Load services from Firestore
        loadServices();

        // Set radio buttons click listener
        radioGroupServiceType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_create_new_service) {
                // Show new service name input, hide spinner
                inputNewServiceName.setVisibility(View.VISIBLE);
                spinnerServices.setVisibility(View.GONE);
            } else if (checkedId == R.id.radio_select_existing_service) {
                // Show spinner, hide new service name input
                inputNewServiceName.setVisibility(View.GONE);
                spinnerServices.setVisibility(View.VISIBLE);
            }
        });

        // Save service to Firestore
        buttonSaveService.setOnClickListener(v -> saveService());
    }

    private void loadServices() {
        db.collection("services")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String serviceName = document.getString("serviceName");
                            if (serviceName != null) {
                                servicesList.add(serviceName);
                            }
                        }
                        // Set adapter for spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateNewServiceActivity.this,
                                android.R.layout.simple_spinner_item, servicesList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerServices.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to load services", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveService() {
        // Get the selected service type
        boolean isCreatingNewService = radioCreateNewService.isChecked();

        // Get common details
        String description = inputDescription.getText().toString().trim();
        String location = inputLocation.getText().toString().trim();
        String price = inputPrice.getText().toString().trim();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Check if all fields are filled
        if (description.isEmpty() || location.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // Handle creating a new service or selecting an existing one
        if (isCreatingNewService) {
            // Get new service name
            String newServiceName = inputNewServiceName.getText().toString().trim();

            if (newServiceName.isEmpty()) {
                Toast.makeText(this, "Please enter a new service name!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save to services collection (new service)
            Map<String, Object> newService = new HashMap<>();
            newService.put("serviceName", newServiceName);
            newService.put("users", new ArrayList<String>());
            newService.put("users", new ArrayList<String>() {{
                add(userId);
            }});

            // Add new service to Firestore
            db.collection("services").document(newServiceName)
                    .set(newService)
                    .addOnSuccessListener(aVoid -> {
                        saveUserService(newServiceName, userId, description, location, price);
                        Toast.makeText(this, getString(R.string.toast_service_created), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, getString(R.string.toast_service_creation_failed), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error creating new service", e);
                    });

        } else {
            // Get selected service
            String selectedService = spinnerServices.getSelectedItem().toString();

            // Add user to existing service's users list
            db.collection("services").document(selectedService)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<String> users = (List<String>) documentSnapshot.get("users");
                            if (users == null) users = new ArrayList<>();

                            if (!users.contains(userId)) {
                                users.add(userId);
                                db.collection("services").document(selectedService)
                                        .update("users", users)
                                        .addOnSuccessListener(aVoid -> {
                                            saveUserService(selectedService, userId, description, location, price);
                                            Toast.makeText(this, getString(R.string.toast_service_created), Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, getString(R.string.toast_service_creation_failed), Toast.LENGTH_SHORT).show();
                                            Log.e("Firestore", "Error updating service users", e);
                                        });
                            } else {
                                Toast.makeText(this, "User already added to this service!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, getString(R.string.toast_service_creation_failed), Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Error retrieving service", e);
                    });
        }
    }

    private void saveUserService(String serviceName, String userId, String description, String location, String price) {
        // Save the user-specific service details in userServices collection
        Map<String, Object> userService = new HashMap<>();
        userService.put("description", description);
        userService.put("location", location);
        userService.put("price", price);
        userService.put("userId", userId);
        userService.put("serviceName", serviceName);

        db.collection("userServices")
                .add(userService)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "User service saved successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving user service", e));
    }
}
