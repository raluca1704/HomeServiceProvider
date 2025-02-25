package com.example.homeserviceprovider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton, registerButton;
    private ImageView eyeIcon;  // ImageView for eye icon
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        eyeIcon = findViewById(R.id.eyeIcon);  // Find the eye icon

        // Set up listeners
        loginButton.setOnClickListener(view -> loginUser());
        registerButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Set the click listener for the eye icon
        eyeIcon.setOnClickListener(view -> togglePasswordVisibility());
    }

    // Method to validate email
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Method to log in the user
    private void loginUser() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(LoginActivity.this, navDrawer.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to toggle password visibility
    private void togglePasswordVisibility() {
        if (passwordField.getInputType() == 129) { // 129 is the input type for password (hidden)
            // Change to show the password
            passwordField.setInputType(1); // 1 is for plain text
            eyeIcon.setImageResource(R.drawable.ic_eye_open); // Change the icon to 'eye open'
        } else {
            // Change to hide the password
            passwordField.setInputType(129); // Set it back to password input type
            eyeIcon.setImageResource(R.drawable.ic_eye); // Change the icon to 'eye closed'
        }

        // Move the cursor to the end of the text
        passwordField.setSelection(passwordField.getText().length());
    }
}
