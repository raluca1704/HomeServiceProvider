package com.example.homeserviceprovider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews;
    private FirebaseFirestore db;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for review item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        if (reviews.isEmpty()) {
            holder.reviewContainer.setVisibility(View.GONE);
            holder.noReviewsContainer.setVisibility(View.VISIBLE);
        } else {
            Review review = reviews.get(position);

            if (review.getReviewText() == null || review.getReviewText().isEmpty()) {
                holder.reviewContainer.setVisibility(View.GONE);
                holder.noReviewsContainer.setVisibility(View.VISIBLE);
            } else {
                holder.reviewContainer.setVisibility(View.VISIBLE);
                holder.noReviewsContainer.setVisibility(View.GONE);

                holder.reviewTextView.setText(review.getReviewText());

                // Obține numele utilizatorului care a lăsat recenzia
                db.collection("Users").document(review.getReviewerId())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String reviewerName = documentSnapshot.getString("name");
                                holder.reviewerNameTextView.setText(
                                        (reviewerName != null ? reviewerName : "Anonymous") + " said:"
                                );
                            } else {
                                holder.reviewerNameTextView.setText("Anonymous said:");
                            }
                        });


            }
        }
    }



    @Override
    public int getItemCount() {
        return reviews.isEmpty() ? 1 : reviews.size(); // Returnăm 1 item pentru mesajul "No reviews available"
    }



    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        LinearLayout reviewContainer; // Container pentru recenzie
        LinearLayout noReviewsContainer; // Container pentru mesajul "No reviews available"
        TextView reviewTextView;
        TextView reviewerNameTextView;
        TextView reviewTimeTextView;
        TextView noReviewsTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewContainer = itemView.findViewById(R.id.reviewContainer);
            noReviewsContainer = itemView.findViewById(R.id.noReviewsContainer);
            reviewTextView = itemView.findViewById(R.id.reviewTextView);
            reviewerNameTextView = itemView.findViewById(R.id.reviewerNameTextView);

            noReviewsTextView = itemView.findViewById(R.id.noReviewsTextView);
        }
    }


}
