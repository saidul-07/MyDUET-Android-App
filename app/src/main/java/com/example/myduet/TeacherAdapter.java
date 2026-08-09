package com.example.myduet;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.databinding.ItemTeacherCardBinding;
import com.example.myduet.models.Teacher;
import java.util.ArrayList;
import java.util.List;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {

    private List<Teacher> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Teacher item);
    }

    public TeacherAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Teacher> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTeacherCardBinding binding = ItemTeacherCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTeacherCardBinding binding;
        private final int[] avatarColors = {
            R.color.accent_blue, R.color.accent_purple, R.color.accent_red,
            R.color.accent_green, R.color.accent_orange, R.color.accent_teal,
            R.color.pgr_bg, R.color.club_bg, R.color.admission_bg
        };

        ViewHolder(ItemTeacherCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Teacher item, OnItemClickListener listener) {
            binding.tvTeacherName.setText(item.getName());
            binding.tvTeacherDesignation.setText(item.getDesignation());
            binding.tvTeacherEmail.setText(item.getEmail());
            binding.tvTeacherOffice.setText(item.getOfficeRoom());

            // Format initials and background color
            String initials = getInitials(item.getName());
            binding.tvAvatarInitials.setText(initials);
            
            int colorIndex = Math.abs(item.getName().hashCode()) % avatarColors.length;
            int color = ContextCompat.getColor(binding.getRoot().getContext(), avatarColors[colorIndex]);
            binding.cardAvatar.setCardBackgroundColor(color);

            if (item.getImage() != null && !item.getImage().isEmpty()) {
                binding.imgTeacherPhoto.setVisibility(android.view.View.VISIBLE);
                com.bumptech.glide.Glide.with(binding.getRoot().getContext())
                        .load(item.getImage())
                        .into(binding.imgTeacherPhoto);
            } else {
                binding.imgTeacherPhoto.setVisibility(android.view.View.GONE);
            }

            binding.getRoot().setOnClickListener(v -> listener.onItemClick(item));
        }

        private String getInitials(String name) {
            if (name == null || name.trim().isEmpty()) return "?";

            // Remove common titles
            String cleaned = name.replaceAll("(?i)\\b(Dr|Md|Mst|Prof|Professor|Associate|Assistant)\\b\\.?", "").trim();
            
            String[] parts = cleaned.split("\\s+");
            StringBuilder sb = new StringBuilder();
            for (String p : parts) {
                if (!p.isEmpty() && Character.isLetter(p.charAt(0))) {
                    sb.append(p.charAt(0));
                }
                if (sb.length() >= 2) break;
            }

            if (sb.length() == 0) return "?";
            return sb.toString().toUpperCase();
        }
    }
}
