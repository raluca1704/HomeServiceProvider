package com.example.homeserviceprovider.ui.account;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homeserviceprovider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class EditProfileActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, addressEditText, phoneEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.edit_name);
        emailEditText = findViewById(R.id.edit_email);
        addressEditText = findViewById(R.id.edit_address);
        phoneEditText = findViewById(R.id.edit_phone);
        saveButton = findViewById(R.id.btn_save_profile);

        // Set existing data
        populateExistingData();

        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

    private void populateExistingData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        nameEditText.setText(task.getResult().getString("name"));
                        emailEditText.setText(task.getResult().getString("email"));
                        addressEditText.setText(task.getResult().getString("address"));
                        phoneEditText.setText(task.getResult().getString("phone"));
                    }
                });
    }

    private void saveProfileChanges() {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId)
                .update("name", name, "email", email, "address", address, "phone", phone)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                        finish();  // Go back to profile activity
                    } else {
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
