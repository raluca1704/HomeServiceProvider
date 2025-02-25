package com.example.homeserviceprovider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {

    private EditText reviewEditText;
    private Button submitReviewButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewEditText = findViewById(R.id.reviewEditText);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        db = FirebaseFirestore.getInstance();

        // Obținem userId din intenția primită
        String userId = getIntent().getStringExtra("userId");

        // Obținem ID-ul utilizatorului logat
        String reviewerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        submitReviewButton.setOnClickListener(view -> {
            String reviewText = reviewEditText.getText().toString().trim();

            if (!reviewText.isEmpty()) {
                // Creăm un timestamp pentru momentul curent
                Timestamp timestamp = Timestamp.now();

                // Creăm referința la utilizatorul care primește recenzia
                DocumentReference reviewedUserRef = db.collection("Users").document(userId);

                // Creăm un obiect Review
                Map<String, Object> newReview = new HashMap<>();
                newReview.put("reviewText", reviewText);
                newReview.put("reviewerId", reviewerId);
                newReview.put("reviewedUserId", reviewedUserRef);
                newReview.put("time", timestamp); // Adăugăm timestamp-ul

                // Adăugăm recenzia în colecția de recenzii
                db.collection("reviews").add(newReview)
                        .addOnSuccessListener(documentReference -> {
                            // Actualizăm lista de recenzii a utilizatorului
                            reviewedUserRef.update("reviews", FieldValue.arrayUnion(newReview))
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(ReviewActivity.this, "Review added successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(ReviewActivity.this, "Error adding review to user.", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ReviewActivity.this, "Error adding review to reviews collection.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(ReviewActivity.this, "Please enter a review.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
