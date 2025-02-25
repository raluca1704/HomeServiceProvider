package com.example.homeserviceprovider;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private Context context;
    private List<User> userList;
    private FirebaseFirestore firestore;
    private UserServiceAdapter userServiceAdapter;  // Adaugă un adapter pentru UserServices
    private List<UserService> userServiceList = new ArrayList<>();

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Setează datele utilizatorului
        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.address.setText(user.getAddress());

        // Gestionarea recenziilor
        if (user.getReviews() != null && !user.getReviews().isEmpty()) {
            StringBuilder reviewsText = new StringBuilder("Reviews: ");
            for (Map<String, Object> reviewMap : user.getReviews()) {
                String reviewText = (String) reviewMap.get("reviewText");
                reviewsText.append(reviewText).append("\n");
            }
            holder.reviews.setText(reviewsText.toString());
        } else {
            holder.reviews.setText("No reviews available");
        }

        // Setează click-ul pe item
        holder.itemView.setOnClickListener(v -> {
            // Creați un Intent pentru a deschide activitatea UserProfileActivity
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("userId", user.getId()); // Trimiți ID-ul utilizatorului
            intent.putExtra("currentUserId", FirebaseAuth.getInstance().getCurrentUser().getUid()); // ID-ul utilizatorului conectat
            context.startActivity(intent);

            // Încarcă serviciile pentru acest utilizator
            fetchUserServices(user.getId());
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Adaugă un nou utilizator în listă
    public void addUsersFromService(String serviceId) {
        firestore.collection("services").document(serviceId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obține lista de utilizatori din documentul serviciului
                        Service service = documentSnapshot.toObject(Service.class);
                        if (service != null && service.getUsers() != null) {
                            // Accesează ID-urile utilizatorilor
                            List<String> userIds = service.getUsers();
                            // Acum obținem datele pentru fiecare utilizator
                            fetchUsers(userIds);
                        }
                    }
                });
    }

    private void fetchUsers(List<String> userIds) {
        firestore.collection("Users")
                .whereIn("id", userIds)  // Interogăm utilizatorii după ID
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            users.add(user);  // Adăugăm utilizatorii la listă
                        }
                    }
                    // Actualizează lista de utilizatori în adapter
                    userList.clear();
                    userList.addAll(users);
                    notifyDataSetChanged();  // Actualizează RecyclerView
                })
                .addOnFailureListener(e -> {
                    // Gestionează erorile de interogare
                    e.printStackTrace();
                });
    }

    // Încarcă serviciile utilizatorului
    private void fetchUserServices(String userId) {
        firestore.collection("userServices")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    userServiceList.clear();  // Curăță lista anterioară
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        UserService userService = document.toObject(UserService.class);
                        if (userService != null) {
                            userServiceList.add(userService);
                        }
                    }
                    if (userServiceAdapter != null) {
                        userServiceAdapter.notifyDataSetChanged();  // Actualizează adapter-ul UserService
                    }
                })
                .addOnFailureListener(e -> {
                    // Gestionează erorile de interogare
                    e.printStackTrace();
                });
    }

    // Adaugă lista de servicii utilizatorului
    public void setUserServiceAdapter(UserServiceAdapter userServiceAdapter) {
        this.userServiceAdapter = userServiceAdapter;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, email, address, reviews;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            address = itemView.findViewById(R.id.address);
            reviews = itemView.findViewById(R.id.reviews);
        }
    }
}
