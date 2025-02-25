package com.example.homeserviceprovider.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homeserviceprovider.Service;
import com.example.homeserviceprovider.ServiceAdapter;
import com.example.homeserviceprovider.UserListActivity;
import com.example.homeserviceprovider.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ServiceAdapter serviceAdapter;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // TextView pentru mesajul "Nu există servicii care se potrivesc"
        TextView noResultsMessage = binding.noResultsMessage;
        noResultsMessage.setVisibility(View.GONE);  // Ascuns de la început

        // Observer pentru lista de servicii
        homeViewModel.getServiceList().observe(getViewLifecycleOwner(), services -> {
            if (serviceAdapter == null) {
                serviceAdapter = new ServiceAdapter(getContext(), services);
                recyclerView.setAdapter(serviceAdapter);

                // Setăm click listener-ul pentru fiecare serviciu
                serviceAdapter.setOnItemClickListener((position, service) -> {
                    // Deschidem UserListActivity și trimitem numele serviciului și lista de utilizatori
                    Intent intent = new Intent(getContext(), UserListActivity.class);
                    intent.putExtra("serviceName", service.getServiceName());
                    intent.putStringArrayListExtra("userIds", new ArrayList<>(service.getUsers()));
                    startActivity(intent);
                });
            } else {
                serviceAdapter.notifyDataSetChanged();
            }

            // Dacă nu sunt servicii găsite, afișează mesajul
            if (services.isEmpty()) {
                recyclerView.setVisibility(View.GONE); // Ascunde RecyclerView
                noResultsMessage.setVisibility(View.VISIBLE); // Arată mesajul
            } else {
                recyclerView.setVisibility(View.VISIBLE); // Arată RecyclerView
                noResultsMessage.setVisibility(View.GONE); // Ascunde mesajul
            }
        });

        // SearchView listener pentru căutarea serviciilor


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Method to hide the keyboard
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getView() != null) {
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }
}
