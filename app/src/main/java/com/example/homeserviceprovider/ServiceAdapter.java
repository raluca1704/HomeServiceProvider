package com.example.homeserviceprovider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private Context context;
    private List<Service> serviceList;
    private OnItemClickListener onItemClickListener;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    // Interfață pentru click-ul pe item
    public interface OnItemClickListener {
        void onItemClick(int position, Service service);
    }

    // Metodă pentru setarea listener-ului
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflați layout-ul item_service
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        // Setează numele serviciului
        holder.serviceName.setText(service.getServiceName());

        // Afișează iconița (dacă există) utilizând Glide (puteți folosi altă bibliotecă sau metodă pentru încărcarea imaginii)
        // Dacă aveți imagini stocate local sau online pentru iconițe, folosiți Glide pentru a le încărca
//        if (service.getIconResId() != 0) {
//            Glide.with(context)
//                    .load(service.getIconResId())  // Dacă iconița este o resursă drawable locală
//                    .into(holder.serviceIcon);
//        }

        // Setează un listener de click pe fiecare item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView serviceIcon;
        TextView serviceName;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            // Referințe către elementele din layout-ul item_service
            serviceIcon = itemView.findViewById(R.id.serviceIcon);
            serviceName = itemView.findViewById(R.id.serviceName);
        }
    }
}
