package com.example.homeserviceprovider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MyMessagesActivity extends AppCompatActivity {

    private ListView conversationsListView;
    private ArrayList<String> conversations; // Nume utilizatori
    private ArrayList<String> chatIds; // ID-urile conversațiilor
    private ConversationsAdapter conversationsAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        conversationsListView = findViewById(R.id.conversationsListView);
        db = FirebaseFirestore.getInstance();
        conversations = new ArrayList<>();
        chatIds = new ArrayList<>();

        conversationsAdapter = new ConversationsAdapter(this, conversations);
        conversationsListView.setAdapter(conversationsAdapter);

        loadConversations();

        conversationsListView.setOnItemClickListener((parent, view, position, id) -> {
            String receiverName = conversations.get(position); // Numele utilizatorului selectat
            String chatId = chatIds.get(position); // ID-ul conversației

            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("chatId", chatId); // Transmite ID-ul conversației
            intent.putExtra("receiverName", receiverName); // Transmite numele utilizatorului
            intent.putExtra("userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        });
    }

    private void loadConversations() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("chats")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    conversations.clear();
                    chatIds.clear();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String senderId = document.getString("senderId");
                        String receiverId = document.getString("receiverId");
                        String senderName = document.getString("senderName");
                        String receiverName = document.getString("receiverName");

                        // Adaugă conversațiile în care utilizatorul curent este fie sender, fie receiver
                        if (currentUserId.equals(senderId)) {
                            conversations.add(receiverName); // Adaugă numele partenerului
                        } else if (currentUserId.equals(receiverId)) {
                            conversations.add(senderName); // Adaugă numele partenerului
                        } else {
                            continue; // Ignoră conversațiile care nu implică utilizatorul curent
                        }

                        chatIds.add(document.getId()); // Adaugă ID-ul conversației
                    }

                    if (conversations.isEmpty()) {
                        Toast.makeText(MyMessagesActivity.this, "No conversations found.", Toast.LENGTH_SHORT).show();
                    }

                    conversationsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("MyMessagesActivity", "Error loading conversations", e);
                    Toast.makeText(MyMessagesActivity.this, "Failed to load conversations.", Toast.LENGTH_SHORT).show();
                });
    }
}