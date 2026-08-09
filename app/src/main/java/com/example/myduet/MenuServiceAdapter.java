package com.example.myduet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MenuServiceAdapter extends RecyclerView.Adapter<MenuServiceAdapter.ServiceViewHolder> {

    public interface OnServiceClickListener {
        void onServiceClick(MenuServiceItem item);
    }

    public static class MenuServiceItem {
        public String title;
        public int iconResId;

        public MenuServiceItem(String title, int iconResId) {
            this.title = title;
            this.iconResId = iconResId;
        }
    }

    private final List<MenuServiceItem> serviceList;
    private final OnServiceClickListener listener;

    public MenuServiceAdapter(List<MenuServiceItem> serviceList, OnServiceClickListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        MenuServiceItem item = serviceList.get(position);
        holder.tvServiceTitle.setText(item.title);
        holder.ivServiceIcon.setImageResource(item.iconResId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onServiceClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivServiceIcon;
        TextView tvServiceTitle;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceIcon = itemView.findViewById(R.id.ivServiceIcon);
            tvServiceTitle = itemView.findViewById(R.id.tvServiceTitle);
        }
    }
}
