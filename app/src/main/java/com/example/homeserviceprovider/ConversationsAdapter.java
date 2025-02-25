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

public class ConversationsAdapter extends ArrayAdapter<String> {

    public ConversationsAdapter(@NonNull Context context, @NonNull List<String> conversations) {
        super(context, 0, conversations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_conversation, parent, false);
        }

        String conversationEntry = getItem(position);
        TextView conversationTextView = convertView.findViewById(R.id.conversationTextView);

        // Afișează numele utilizatorului
        conversationTextView.setText(conversationEntry);

        return convertView;
    }
}