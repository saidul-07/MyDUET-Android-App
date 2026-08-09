package com.example.myduet;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.databinding.ItemDeptCardBinding;
import com.example.myduet.models.DepartmentInfo;
import java.util.ArrayList;
import java.util.List;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {

    private List<DepartmentInfo> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(DepartmentInfo item);
    }

    public DepartmentAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<DepartmentInfo> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDeptCardBinding binding = ItemDeptCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);

        // Entrance animation: Fade and Scale
        holder.itemView.setAlpha(0.0f);
        holder.itemView.setScaleX(0.9f);
        holder.itemView.setScaleY(0.9f);
        holder.itemView.animate()
                .alpha(1.0f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(350)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDeptCardBinding binding;

        ViewHolder(ItemDeptCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DepartmentInfo item, OnItemClickListener listener) {
            String key = item.getKey().toLowerCase();
            
            // Map short code name and full name
            String code = key.toUpperCase();
            if (key.equals("hss")) {
                code = "HSS";
            } else if (key.equals("math")) {
                code = "MATH";
            } else if (key.equals("chem")) {
                code = "CHEM";
            } else if (key.equals("phy")) {
                code = "PHYS";
            }
            binding.tvDeptCode.setText(code);
            binding.tvDeptFullName.setText(item.getName());
            binding.tvTeacherCount.setText(item.getTeacherCount() + " Teachers");

            // Map emoji based on key
            String emoji = "🏫";
            String accentColorHex = "#005FB0";
            String bgTintHex = "#E3F2FD";

            switch (key) {
                case "cse":
                    emoji = "💻";
                    accentColorHex = "#005FB0"; // Blue
                    bgTintHex = "#E3F2FD";
                    break;
                case "eee":
                    emoji = "⚡";
                    accentColorHex = "#7B1FA2"; // Purple
                    bgTintHex = "#F3E5F5";
                    break;
                case "ce":
                    emoji = "🏗️";
                    accentColorHex = "#E65100"; // Orange
                    bgTintHex = "#FFF3E0";
                    break;
                case "me":
                    emoji = "⚙️";
                    accentColorHex = "#C62828"; // Red
                    bgTintHex = "#FFEBEE";
                    break;
                case "te":
                    emoji = "🧵";
                    accentColorHex = "#2E7D32"; // Green
                    bgTintHex = "#E8F5E9";
                    break;
                case "arch":
                    emoji = "📐";
                    accentColorHex = "#C2185B"; // Pink
                    bgTintHex = "#FCE4EC";
                    break;
                case "ipe":
                    emoji = "🏭";
                    accentColorHex = "#FF8F00"; // Amber
                    bgTintHex = "#FFF8E1";
                    break;
                case "fe":
                    emoji = "🌾";
                    accentColorHex = "#00695C"; // Emerald
                    bgTintHex = "#E0F2F1";
                    break;
                case "che":
                    emoji = "🧪";
                    accentColorHex = "#00838F"; // Cyan
                    bgTintHex = "#E0F7FA";
                    break;
                case "math":
                    emoji = "🧮";
                    accentColorHex = "#D32F2F"; // Red
                    bgTintHex = "#FFEBEE";
                    break;
                case "chem":
                    emoji = "⚗️";
                    accentColorHex = "#388E3C"; // Green
                    bgTintHex = "#E8F5E9";
                    break;
                case "phy":
                    emoji = "⚛️";
                    accentColorHex = "#0288D1"; // Light Blue
                    bgTintHex = "#E1F5FE";
                    break;
                case "hss":
                    emoji = "📚";
                    accentColorHex = "#8E24AA"; // Purple
                    bgTintHex = "#F3E5F5";
                    break;
            }

            binding.tvDeptEmoji.setText(emoji);

            // Apply colors
            int accentColor = Color.parseColor(accentColorHex);
            int bgTint = Color.parseColor(bgTintHex);

            binding.cardIconContainer.setCardBackgroundColor(bgTint);
            binding.tvViewFacultyLink.setTextColor(accentColor);

            binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
