package com.example.myduet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.R;
import com.example.myduet.models.NoticeEntity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    private List<NoticeEntity> notices = new ArrayList<>();
    private OnNoticeClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

    public interface OnNoticeClickListener {
        void onReadMoreClick(NoticeEntity notice);
    }

    public void setNotices(List<NoticeEntity> notices) {
        this.notices = notices;
        notifyDataSetChanged();
    }

    public void setOnNoticeClickListener(OnNoticeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_item, parent, false);
        return new NoticeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        NoticeEntity notice = notices.get(position);
        holder.tvCategory.setText("📢 " + notice.getCategory());
        holder.tvTitle.setText(notice.getTitle());
        holder.tvDescription.setText(notice.getDescription());
        holder.tvDate.setText(notice.getPublishDate());
        
        // Show NEW badge if notice is within the last 7 days
        try {
            Date publishDate = dateFormat.parse(notice.getPublishDate());
            if (publishDate != null) {
                long diff = System.currentTimeMillis() - publishDate.getTime();
                long diffDays = diff / (1000 * 60 * 60 * 24);
                if (diffDays >= 0 && diffDays <= 7) {
                    holder.cardNewBadge.setVisibility(View.VISIBLE);
                } else {
                    holder.cardNewBadge.setVisibility(View.GONE);
                }
            } else {
                holder.cardNewBadge.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.cardNewBadge.setVisibility(View.GONE);
        }

        holder.btnReadMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReadMoreClick(notice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvDescription, tvDate;
        MaterialCardView cardNewBadge;
        MaterialButton btnReadMore;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            cardNewBadge = itemView.findViewById(R.id.cardNewBadge);
            btnReadMore = itemView.findViewById(R.id.btnReadMore);
        }
    }
}