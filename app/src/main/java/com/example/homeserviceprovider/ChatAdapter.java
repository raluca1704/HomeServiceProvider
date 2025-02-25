package com.example.homeserviceprovider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    private String currentUserId;

    public ChatAdapter(@NonNull Context context, @NonNull List<ChatMessage> messages, String currentUserId) {
        super(context, 0, messages);
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        // Determină tipul mesajului (trimis sau primit)
        ChatMessage message = getItem(position);
        return message != null && message.getSenderId().equals(currentUserId) ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        // Sunt două tipuri de vizualizări: mesaje trimise și mesaje primite
        return 2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatMessage message = getItem(position);
        boolean isSentByCurrentUser = message != null && message.getSenderId().equals(currentUserId);

        // Alege layout-ul corespunzător în funcție de cine a trimis mesajul
        if (convertView == null) {
            int layoutId = isSentByCurrentUser ? R.layout.item_message_sent : R.layout.item_message_received;
            convertView = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);
        }

        // Populează textul mesajului în layout
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        if (message != null) {
            messageTextView.setText(message.getText());
        }

        return convertView;
    }
}