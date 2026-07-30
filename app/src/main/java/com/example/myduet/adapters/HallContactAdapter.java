package com.example.myduet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.R;
import com.example.myduet.models.EmergencyContact;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class HallContactAdapter extends RecyclerView.Adapter<HallContactAdapter.ViewHolder> {

    private List<EmergencyContact> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onCallClick(EmergencyContact contact);
        void onDetailsClick(EmergencyContact contact);
    }

    public HallContactAdapter(List<EmergencyContact> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hall_contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyContact contact = list.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhone());
        
        // Handle Designation and Hall Type
        // We use assistantName for hall type and personName for provost info if available
        if (contact.getName().contains("Madam Curie")) {
            holder.tvType.setText("Female Residential Hall");
            holder.ivIcon.setColorFilter(0xFF7B1FA2); // Purple
            holder.iconCard.setCardBackgroundColor(0xFFF3E8FF);
            holder.ivPhoneIcon.setColorFilter(0xFF7B1FA2);
        } else if (contact.getName().contains("Bijoy 24")) {
            holder.tvType.setText("Male Residential Hall");
            holder.ivIcon.setColorFilter(0xFF1565C0); // Blue
            holder.iconCard.setCardBackgroundColor(0xFFE8F1FF);
            holder.ivPhoneIcon.setColorFilter(0xFF1565C0);
        } else if (contact.getName().contains("Kazi Nazrul")) {
            holder.tvType.setText("Male Residential Hall");
            holder.ivIcon.setColorFilter(0xFF2E7D32); // Green
            holder.iconCard.setCardBackgroundColor(0xFFE8F8EE);
            holder.ivPhoneIcon.setColorFilter(0xFF2E7D32);
        } else if (contact.getName().contains("Shaheed Tazuddin")) {
            holder.tvType.setText("Male Residential Hall");
            holder.ivIcon.setColorFilter(0xFFEF6C00); // Orange
            holder.iconCard.setCardBackgroundColor(0xFFFFF3E0);
            holder.ivPhoneIcon.setColorFilter(0xFFEF6C00);
        } else if (contact.getName().contains("Shahid Muktijodda")) {
            holder.tvType.setText("Male Residential Hall");
            holder.ivIcon.setColorFilter(0xFFE53935); // Red
            holder.iconCard.setCardBackgroundColor(0xFFFFE8E8);
            holder.ivPhoneIcon.setColorFilter(0xFFE53935);
        }

        holder.btnCall.setOnClickListener(v -> listener.onCallClick(contact));
        holder.btnDetails.setOnClickListener(v -> listener.onDetailsClick(contact));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvAdminLabel, tvPhone;
        ImageView ivIcon, ivPhoneIcon;
        MaterialCardView iconCard;
        View btnCall, btnDetails;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvHallName);
            tvType = view.findViewById(R.id.tvHallType);
            tvAdminLabel = view.findViewById(R.id.tvAdminLabel);
            tvPhone = view.findViewById(R.id.tvPhone);
            ivIcon = view.findViewById(R.id.ivIcon);
            ivPhoneIcon = view.findViewById(R.id.ivPhoneIcon);
            iconCard = view.findViewById(R.id.iconCard);
            btnCall = view.findViewById(R.id.btnCall);
            btnDetails = view.findViewById(R.id.btnDetails);
        }
    }
}