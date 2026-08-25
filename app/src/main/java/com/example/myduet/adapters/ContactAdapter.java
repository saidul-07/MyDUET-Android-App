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

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<EmergencyContact> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EmergencyContact contact);
    }

    public ContactAdapter(List<EmergencyContact> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyContact contact = list.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvPhone.setText(contact.getPhone());
        
        if (holder.ivIcon != null) {
            int iconRes = contact.getIconResId();
            if (iconRes != 0) {
                holder.ivIcon.setImageResource(iconRes);
                if (holder.cardIcon != null) {
                    holder.cardIcon.setVisibility(View.VISIBLE);
                }
            } else {
                holder.ivIcon.setImageResource(R.drawable.ic_office);
                if (holder.cardIcon != null) {
                    holder.cardIcon.setVisibility(View.VISIBLE);
                }
            }
        }
        
        holder.itemView.setOnClickListener(v -> listener.onItemClick(contact));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;
        android.widget.ImageView ivIcon;
        com.google.android.material.card.MaterialCardView cardIcon;

        ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tvContactName);
            tvPhone = view.findViewById(R.id.tvContactPhone);
            ivIcon = view.findViewById(R.id.ivContactIcon);
            cardIcon = view.findViewById(R.id.cardIcon);
        }
    }
}