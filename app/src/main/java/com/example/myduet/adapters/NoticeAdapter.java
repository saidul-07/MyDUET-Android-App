package com.example.myduet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.R;
import com.example.myduet.models.Notice;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    private List<Notice> notices = new ArrayList<>();
    private OnNoticeClickListener listener;

    public interface OnNoticeClickListener {
        void onReadMoreClick(Notice notice);
    }

    public void setNotices(List<Notice> notices) {
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
        Notice notice = notices.get(position);
        holder.tvCategory.setText("📢 " + notice.getCategory());
        holder.tvTitle.setText(notice.getTitle());
        holder.tvDescription.setText(notice.getDescription());
        holder.tvDate.setText(notice.getDate());
        
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
        MaterialButton btnReadMore;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnReadMore = itemView.findViewById(R.id.btnReadMore);
        }
    }
}