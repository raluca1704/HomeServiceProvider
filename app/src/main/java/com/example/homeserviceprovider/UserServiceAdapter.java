package com.example.homeserviceprovider;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class UserServiceAdapter extends RecyclerView.Adapter<UserServiceAdapter.ViewHolder> {

    private final Context context;
    private final List<UserService> userServiceList;

    public UserServiceAdapter(Context context, List<UserService> userServiceList) {
        this.context = context;
        this.userServiceList = userServiceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserService userService = userServiceList.get(position);
        holder.serviceNameTextView.setText(userService.getServiceName());
        holder.descriptionTextView.setText(userService.getDescription());
        holder.locationTextView.setText("Location: " + userService.getLocation());
        holder.priceTextView.setText("Price: " + userService.getPrice());

        // Click listener pentru a deschide UserProfileActivity
        holder.itemView.setOnClickListener(v -> {
            // Creăm un Intent pentru a deschide UserProfileActivity
            Intent intent = new Intent(context, UserProfileActivity.class);
            // Se presupune că UserService conține un ID de utilizator, adăugați acest ID în intent
            intent.putExtra("serviceName", userService.getServiceName());
            intent.putExtra("userId", userService.getUserId());
            // Asigură-te că adaugi și `serviceId`, dacă este necesar
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return userServiceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceNameTextView, descriptionTextView, locationTextView, priceTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceNameTextView = itemView.findViewById(R.id.serviceNameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
