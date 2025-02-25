package com.example.homeserviceprovider.ui.account;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homeserviceprovider.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, emailTextView, addressTextView, phoneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = findViewById(R.id.user_name);
        emailTextView = findViewById(R.id.user_email);
        addressTextView = findViewById(R.id.user_address);
        phoneTextView = findViewById(R.id.user_phone);

        // Fetch user data from Firestore
        fetchUserData();

        // Set up button to edit profile
        findViewById(R.id.btn_edit_profile).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserData() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String address = document.getString("address");
                            String phone = document.getString("phone");

                            nameTextView.setText(name != null ? name : "Name not available");
                            emailTextView.setText(email != null ? email : "Email not available");
                            addressTextView.setText(address != null ? address : "Address not available");
                            phoneTextView.setText(phone != null ? phone : "Phone not available");
                        }
                    }
                });
    }
}
