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
                    accentColorHex = "#088BB3";
                    bgTintHex = "#D7EFF7";
                    break;
                case "eee":
                    emoji = "⚡";
                    accentColorHex = "#444A72";
                    bgTintHex = "#DCE0EE";
                    break;
                case "ce":
                    emoji = "🏗️";
                    accentColorHex = "#088BB3";
                    bgTintHex = "#D7EFF7";
                    break;
                case "me":
                    emoji = "⚙️";
                    accentColorHex = "#7DD6C8";
                    bgTintHex = "#DCF6F2";
                    break;
                case "te":
                    emoji = "🧵";
                    accentColorHex = "#444A72";
                    bgTintHex = "#DCE0EE";
                    break;
                case "arch":
                    emoji = "📐";
                    accentColorHex = "#444A72";
                    bgTintHex = "#DCE0EE";
                    break;
                case "ipe":
                    emoji = "🏭";
                    accentColorHex = "#7DD6C8";
                    bgTintHex = "#DCF6F2";
                    break;
                case "fe":
                    emoji = "🌾";
                    accentColorHex = "#7DD6C8";
                    bgTintHex = "#DCF6F2";
                    break;
                case "che":
                    emoji = "🧪";
                    accentColorHex = "#088BB3";
                    bgTintHex = "#D7EFF7";
                    break;
                case "math":
                    emoji = "🧮";
                    accentColorHex = "#444A72";
                    bgTintHex = "#DCE0EE";
                    break;
                case "chem":
                    emoji = "⚗️";
                    accentColorHex = "#7DD6C8";
                    bgTintHex = "#DCF6F2";
                    break;
                case "phy":
                    emoji = "⚛️";
                    accentColorHex = "#088BB3";
                    bgTintHex = "#D7EFF7";
                    break;
                case "hss":
                    emoji = "📚";
                    accentColorHex = "#444A72";
                    bgTintHex = "#DCE0EE";
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
