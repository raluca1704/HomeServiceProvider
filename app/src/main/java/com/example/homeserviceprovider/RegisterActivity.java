package com.example.homeserviceprovider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField, emailField, passwordField, confirmPasswordField, phoneField, addressField;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Connect UI components to Java variables
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmPasswordField);
        phoneField = findViewById(R.id.phoneField);
        addressField = findViewById(R.id.addressField);
        registerButton = findViewById(R.id.registerButton);

        // Set click listener for register button
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();
        String phone = phoneField.getText().toString().trim();
        String address = addressField.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            nameField.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Passwords do not match");
            return;
        }

        if (TextUtils.isEmpty(phone) || phone.length() < 10) {
            phoneField.setError("Enter a valid phone number");
            return;
        }

        if (TextUtils.isEmpty(address)) {
            addressField.setError("Address is required");
            return;
        }

        // Create user in Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the user ID
                        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                        // Save user data to Firestore
                        saveUserDataToFirestore(userId, name, email, phone, address);
                    } else {
                        Toast.makeText(this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataToFirestore(String userId, String name, String email, String phone, String address) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("address", address);

        firestore.collection("Users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    // Redirect to main or login activity
                    Intent intent = new Intent(RegisterActivity.this, navDrawer.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore Error", e.getMessage());
                    Toast.makeText(this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
