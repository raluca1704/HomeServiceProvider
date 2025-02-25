package com.example.homeserviceprovider;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private EditText messageEditText;
    private Button sendMessageButton;
    private ListView messagesListView;
    private FirebaseFirestore db;
    private String chatId;
    private String userId;
    private String receiverName;
    private List<ChatMessage> messages;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageEditText = findViewById(R.id.messageEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        messagesListView = findViewById(R.id.messagesListView);

        db = FirebaseFirestore.getInstance();

        chatId = getIntent().getStringExtra("chatId");
        userId = getIntent().getStringExtra("userId");
        receiverName = getIntent().getStringExtra("receiverName");

        if (chatId == null || userId == null || receiverName == null) {
            Toast.makeText(this, "Invalid chat details!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messages, userId);
        messagesListView.setAdapter(chatAdapter);

        loadMessages();

        sendMessageButton.setOnClickListener(view -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
            } else {
                Toast.makeText(ChatActivity.this, "Please enter a message.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp") // Asigură ordonarea mesajelor
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(ChatActivity.this, "Error loading messages.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (querySnapshot != null) {
                        messages.clear();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            String senderId = document.getString("senderId");
                            String message = document.getString("text");
                            Object timestampObj = document.get("timestamp"); // Verificăm timestamp-ul

                            long timestamp = 0;
                            if (timestampObj instanceof Long) {
                                timestamp = (Long) timestampObj; // Dacă e deja un Long
                            } else if (timestampObj instanceof com.google.firebase.Timestamp) {
                                timestamp = ((com.google.firebase.Timestamp) timestampObj).toDate().getTime(); // Convertim Timestamp
                            }

                            if (message != null && senderId != null) {
                                messages.add(new ChatMessage(senderId, message, timestamp));
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        messagesListView.setSelection(messages.size() - 1); // Scroll la ultimul mesaj
                    }
                });
    }
    private void sendMessage(String messageText) {
        String messageId = db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document().getId();

        ChatMessage newMessage = new ChatMessage(userId, messageText, System.currentTimeMillis());

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .document(messageId)
                .set(newMessage)
                .addOnSuccessListener(aVoid -> {
                    messageEditText.setText("");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this, "Error sending message.", Toast.LENGTH_SHORT).show();
                });
        db.collection("chats")
                .document(chatId)
                .update("lastMessage", messageText, "timestamp", System.currentTimeMillis());
    }
}