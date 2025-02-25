package com.example.homeserviceprovider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.homeserviceprovider.ui.account.ProfileActivity;
import com.example.homeserviceprovider.ui.createnewservice.CreateNewServiceActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.homeserviceprovider.databinding.ActivityNavDrawerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class navDrawer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavDrawerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavDrawer.toolbar);
        binding.appBarNavDrawer.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });

        // Initialize the DrawerLayout and NavigationView
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Set up Navigation with NavigationController
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav_drawer);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Set user data in the navigation header
        setUserDataInNavHeader(navigationView);

        // Add Navigation Item Click Listener
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_create_service) {
                    startActivity(new Intent(navDrawer.this, CreateNewServiceActivity.class));
                } else if (id == R.id.nav_my_account) {
                    startActivity(new Intent(navDrawer.this, ProfileActivity.class));
                } else if (id == R.id.nav_my_messages) {
                    // Navigate to MyMessagesActivity
                    startActivity(new Intent(navDrawer.this, MyMessagesActivity.class));
                } else if (id == R.id.nav_logout) {
                    // Perform logout action
                    FirebaseAuth.getInstance().signOut(); // Logout from Firebase
                    Intent intent = new Intent(navDrawer.this, LoginActivity.class); // Replace with your login activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear activity stack
                    startActivity(intent);
                    finish(); // Close current activity
                }

                // Close the navigation drawer after item is selected
                DrawerLayout drawer = binding.drawerLayout;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_nav_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Function to set the user name and email in the Navigation Drawer header
    private void setUserDataInNavHeader(NavigationView navigationView) {
        // Access TextViews in the header
        View headerView = navigationView.getHeaderView(0);
        TextView nameTextView = headerView.findViewById(R.id.user_name);
        TextView emailTextView = headerView.findViewById(R.id.user_email);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid(); // Get user ID

            // Fetch firstName and lastName from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("name");
                                String email = document.getString("email");
                                if (name != null && email != null) {
                                    nameTextView.setText(name);
                                    emailTextView.setText(email);
                                } else {
                                    nameTextView.setText("Name not available");
                                    emailTextView.setText("Email not available");
                                }
                            } else {
                                Log.e("Firestore", "Document not found for user: " + userId);
                                nameTextView.setText("Document not found");
                            }
                        } else {
                            Log.e("Firestore", "Error getting document", task.getException());
                            nameTextView.setText("Error loading name");
                            emailTextView.setText("Error loading email");
                        }
                    });

        }
    }
}