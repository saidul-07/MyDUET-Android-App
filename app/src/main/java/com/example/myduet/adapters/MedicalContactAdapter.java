package com.example.myduet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.R;
import com.example.myduet.models.EmergencyContact;
import java.util.List;

public class MedicalContactAdapter extends RecyclerView.Adapter<MedicalContactAdapter.ViewHolder> {

    private List<EmergencyContact> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onCallClick(EmergencyContact contact);
        void onDetailsClick(EmergencyContact contact);
    }

    public MedicalContactAdapter(List<EmergencyContact> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medical_contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyContact contact = list.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhone());
        holder.tvHours.setText(contact.getHours());
        holder.tvLocation.setText(contact.getLocation());
        
        // Handle Designation and Person Name
        if (contact.getAssistantName() != null && !contact.getAssistantName().isEmpty()) {
            holder.tvDesignation.setText(contact.getAssistantName());
            holder.tvDescription.setText(contact.getPersonName());
        } else {
            holder.tvDesignation.setText("Emergency Contact");
            holder.tvDescription.setText(contact.getPersonName());
        }

        holder.btnCall.setOnClickListener(v -> listener.onCallClick(contact));
        holder.btnDetails.setOnClickListener(v -> listener.onDetailsClick(contact));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesignation, tvDescription, tvPhone, tvHours, tvLocation;
        View btnCall, btnDetails;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvContactName);
            tvDesignation = view.findViewById(R.id.tvDesignation);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvPhone = view.findViewById(R.id.tvPhone);
            tvHours = view.findViewById(R.id.tvHours);
            tvLocation = view.findViewById(R.id.tvLocation);
            btnCall = view.findViewById(R.id.btnCall);
            btnDetails = view.findViewById(R.id.btnDetails);
        }
    }
}