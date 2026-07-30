package com.example.myduet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.R;
import com.example.myduet.models.EmergencyCategory;
import java.util.List;

public class EmergencyCategoryAdapter extends RecyclerView.Adapter<EmergencyCategoryAdapter.ViewHolder> {

    private List<EmergencyCategory> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(EmergencyCategory item);
    }

    public EmergencyCategoryAdapter(List<EmergencyCategory> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.emergency_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyCategory item = list.get(position);
        holder.tvName.setText(item.getName());
        holder.tvDescription.setText(item.getDescription());
        holder.tvCount.setText(item.getCount());
        holder.ivIcon.setImageResource(item.getIconRes());
        
        // Update icon container colors
        holder.iconCard.setCardBackgroundColor(item.getBgColor());
        holder.ivIcon.setColorFilter(item.getIconTint());
        
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvDescription, tvCount;
        com.google.android.material.card.MaterialCardView iconCard;

        ViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.ivIcon);
            tvName = view.findViewById(R.id.tvName);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvCount = view.findViewById(R.id.tvCount);
            iconCard = view.findViewById(R.id.iconCard);
        }
    }
}