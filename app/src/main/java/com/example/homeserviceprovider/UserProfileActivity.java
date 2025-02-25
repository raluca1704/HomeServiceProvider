package com.example.homeserviceprovider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private TextView userDetailsTitleTextView, userAddressTextView, userEmailTextView, userPhoneTextView, userReviewsTextView;
    private TextView serviceDescriptionTextView, serviceLocationTextView, servicePriceTextView;
    private ProgressBar progressBar;
    private Button reviewButton, chatButton, seeReviewsButton;
    private LinearLayout reviewsContainer;
    private RecyclerView reviewsRecyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewsList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Inițializăm elementele UI
        userDetailsTitleTextView = findViewById(R.id.userDetailsTitleTextView);
        userAddressTextView = findViewById(R.id.userAddressTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);
        userPhoneTextView = findViewById(R.id.userPhoneTextView);
        userReviewsTextView = findViewById(R.id.userReviewsTextView);
        serviceDescriptionTextView = findViewById(R.id.serviceDescriptionTextView);
        serviceLocationTextView = findViewById(R.id.serviceLocationTextView);
        servicePriceTextView = findViewById(R.id.servicePriceTextView);
        progressBar = findViewById(R.id.progressBar);
        reviewButton = findViewById(R.id.reviewButton);
        chatButton = findViewById(R.id.chatButton);
        seeReviewsButton = findViewById(R.id.seeReviewsButton);
        reviewsContainer = findViewById(R.id.reviewsContainer);
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);

        reviewsList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewsList);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setAdapter(reviewAdapter);

        db = FirebaseFirestore.getInstance();

        String userId = getIntent().getStringExtra("userId");
        String serviceName = getIntent().getStringExtra("serviceName");

        Log.d("UserProfileActivity", "UserId: " + userId + " ServiceName: " + serviceName);

        if (userId != null) {
            loadUserDetails(userId);
            loadServiceDetails(userId, serviceName);
        } else {
            Toast.makeText(this, "Missing user or service information.", Toast.LENGTH_SHORT).show();
        }

        reviewButton.setOnClickListener(view -> {
            Intent reviewIntent = new Intent(UserProfileActivity.this, ReviewActivity.class);
            reviewIntent.putExtra("userId", userId);
            startActivity(reviewIntent);
        });

        chatButton.setOnClickListener(view -> {
            if (userId != null) {
                String receiverName = userDetailsTitleTextView.getText().toString().replace("Details about ", "").trim();
                openOrCreateChat(userId, receiverName);
            } else {
                Toast.makeText(this, "User ID is missing.", Toast.LENGTH_SHORT).show();
            }
        });

        seeReviewsButton.setOnClickListener(v -> {
            if (reviewsContainer.getVisibility() == View.GONE) {
                reviewsContainer.setVisibility(View.VISIBLE);
                loadReviews(userId);
            } else {
                reviewsContainer.setVisibility(View.GONE);
            }
        });
    }

    private void loadUserDetails(String userId) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("Users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        String address = documentSnapshot.getString("address");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phone");

                        userDetailsTitleTextView.setText("Details about " + name);
                        userAddressTextView.setText("Address: " + address);
                        userEmailTextView.setText("Email: " + email);
                        userPhoneTextView.setText("Phone: " + phone);
                    } else {
                        Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user details.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void loadServiceDetails(String userId, String serviceName) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("userServices")
                .whereEqualTo("userId", userId)
                .whereEqualTo("serviceName", serviceName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String description = document.getString("description");
                        String location = document.getString("location");
                        String price = document.getString("price");

                        serviceDescriptionTextView.setText("Description: " + description);
                        serviceLocationTextView.setText("Location: " + location);
                        servicePriceTextView.setText("Price: " + price);
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading service details.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void loadReviews(String userId) {
        progressBar.setVisibility(View.VISIBLE);

        // Accesăm câmpul "reviews" din colecția "Users"
        db.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        reviewsList.clear();
                        // Obținem câmpul "reviews" din documentul utilizatorului
                        Object reviewsObj = documentSnapshot.get("reviews");
                        if (reviewsObj instanceof List) {
                            List<Map<String, Object>> reviews = (List<Map<String, Object>>) reviewsObj;
                            if (reviews != null && !reviews.isEmpty()) {
                                for (Map<String, Object> reviewData : reviews) {
                                    String reviewText = (String) reviewData.get("reviewText");
                                    String reviewerId = (String) reviewData.get("reviewerId");
                                    Object timeObj = reviewData.get("time");
                                    Timestamp time = null;
                                    if (timeObj instanceof Timestamp) {
                                        time = (Timestamp) timeObj;
                                    }
                                    Review review = new Review(reviewerId, null, reviewText, time);
                                    reviewsList.add(review);
                                }
                            }
                        }
                        reviewAdapter.notifyDataSetChanged();
                    }
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading reviews.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }
    private void openOrCreateChat(String receiverId, String receiverName) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("chats")
                .whereEqualTo("senderId", currentUserId)
                .whereEqualTo("receiverId", receiverId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String chatId = document.getId();
                            openChatActivity(chatId, receiverName);
                            return;
                        }
                    } else {
                        db.collection("chats")
                                .whereEqualTo("receiverId", currentUserId)
                                .whereEqualTo("senderId", receiverId)
                                .get()
                                .addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful() && !innerTask.getResult().isEmpty()) {
                                        for (QueryDocumentSnapshot document : innerTask.getResult()) {
                                            String chatId = document.getId();
                                            openChatActivity(chatId, receiverName);
                                            return;
                                        }
                                    } else {
                                        createNewChat(receiverId, receiverName, currentUserId);
                                    }
                                });
                    }
                });
    }

    private void createNewChat(String receiverId, String receiverName, String senderId) {
        db.collection("Users").document(senderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String senderName = documentSnapshot.getString("name");
                        if (senderName == null) {
                            senderName = "Unknown";
                        }

                        String chatId = db.collection("chats").document().getId();

                        Map<String, Object> chatData = new HashMap<>();
                        chatData.put("lastMessage", "");
                        chatData.put("receiverId", receiverId);
                        chatData.put("receiverName", receiverName);
                        chatData.put("senderId", senderId);
                        chatData.put("senderName", senderName);
                        chatData.put("timestamp", System.currentTimeMillis());

                        db.collection("chats").document(chatId).set(chatData)
                                .addOnSuccessListener(aVoid -> openChatActivity(chatId, receiverName))
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to create chat.", Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Sender not found in Users collection.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch sender details.", Toast.LENGTH_SHORT).show());
    }

    private void openChatActivity(String chatId, String receiverName) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("receiverName", receiverName);
        intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        startActivity(intent);
    }
}