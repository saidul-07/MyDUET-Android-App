package com.example.myduet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarEventAdapter extends RecyclerView.Adapter<CalendarEventAdapter.EventViewHolder> {

    private final List<CalenderActivity.CalendarEvent> eventList;

    public CalendarEventAdapter(List<CalenderActivity.CalendarEvent> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        CalenderActivity.CalendarEvent event = eventList.get(position);
        holder.tvEventTitle.setText(event.title);
        holder.tvEventDurationBadge.setText(String.format(Locale.getDefault(), "%d", event.duration));
        
        String startFormatted = formatDate(event.startDate);
        boolean isBangla = Locale.getDefault().getLanguage().equals("bn");
        String prefix = isBangla ? "ছুটির পরিমাণঃ " : "Duration: ";
        String toText = isBangla ? " থেকে " : " to ";

        if (event.duration == 1) {
            holder.tvEventRange.setText(prefix + startFormatted);
        } else {
            String endFormatted = formatDate(event.endDate);
            holder.tvEventRange.setText(prefix + startFormatted + toText + endFormatted);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = fromUser.parse(dateStr);
            if (date != null) {
                return myFormat.format(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventDurationBadge;
        TextView tvEventTitle;
        TextView tvEventRange;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventDurationBadge = itemView.findViewById(R.id.tvEventDurationBadge);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            tvEventRange = itemView.findViewById(R.id.tvEventRange);
        }
    }
}
