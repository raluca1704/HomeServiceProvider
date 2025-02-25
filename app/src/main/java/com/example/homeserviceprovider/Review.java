package com.example.homeserviceprovider;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Date;

public class Review {
    private String reviewerId; // ID-ul utilizatorului care a lăsat recenzia
    private DocumentReference reviewedUserId; // Referință către utilizatorul care a primit recenzia
    private String reviewText; // Textul recenziei
    private Timestamp time; // Timpul în care a fost lăsată recenzia

    // Constructor
    public Review(String reviewerId, DocumentReference reviewedUserId, String reviewText, Timestamp time) {
        this.reviewerId = reviewerId;
        this.reviewedUserId = reviewedUserId;
        this.reviewText = reviewText;
        this.time = time;
    }
    public Review(String reviewText) {
        this.reviewerId = "No reviews";  // Folosim un ID generic pentru recenzii "No reviews"
        this.reviewedUserId = null;      // Nu există un DocumentReference
        this.reviewText = reviewText;
        this.time = null;           // Nu avem timestamp
    }

    // Getter și Setter
    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public DocumentReference getReviewedUserId() {
        return reviewedUserId;
    }

    public void setReviewedUserId(DocumentReference reviewedUserId) {
        this.reviewedUserId = reviewedUserId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTimestamp(Timestamp times) {
        this.time = time;
    }
}
