package com.example.myduet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myduet.R;
import com.example.myduet.databinding.ItemAboutMemberBinding;
import com.example.myduet.databinding.ItemAboutYearHeaderBinding;
import com.example.myduet.models.AboutMember;

import java.util.List;

public class AboutMemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<AboutMember> items;

    public AboutMemberAdapter(List<AboutMember> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getItemType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == AboutMember.TYPE_HEADER) {
            ItemAboutYearHeaderBinding binding = ItemAboutYearHeaderBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(binding);
        } else {
            ItemAboutMemberBinding binding = ItemAboutMemberBinding.inflate(inflater, parent, false);
            return new MemberViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AboutMember item = items.get(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else if (holder instanceof MemberViewHolder) {
            ((MemberViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final ItemAboutYearHeaderBinding binding;

        public HeaderViewHolder(ItemAboutYearHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AboutMember item) {
            binding.tvYearHeader.setText(item.getYear());
        }
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        private final ItemAboutMemberBinding binding;

        public MemberViewHolder(ItemAboutMemberBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(AboutMember member) {
            binding.tvMemberName.setText(member.getName());
            binding.tvAppRole.setText(member.getAppRole());

            if (member.getSeries() != null && !member.getSeries().trim().isEmpty()) {
                binding.tvSeries.setVisibility(View.VISIBLE);
                binding.tvSeries.setText(member.getSeries());
            } else {
                binding.tvSeries.setVisibility(View.GONE);
            }

            if (member.hasCurrentWork()) {
                binding.layoutCurrentWork.setVisibility(View.VISIBLE);
                binding.tvCurrentRole.setText(member.getCurrentRole());
                binding.tvCompany.setText(member.getCompany());
            } else {
                binding.layoutCurrentWork.setVisibility(View.GONE);
            }

            if (member.getImageUrl() != null && !member.getImageUrl().trim().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(member.getImageUrl())
                        .circleCrop()
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(binding.ivMemberAvatar);
            } else {
                binding.ivMemberAvatar.setImageResource(R.drawable.ic_profile);
            }
        }
    }
}