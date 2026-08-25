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
        
        String dateFormatted;
        boolean isBangla = Locale.getDefault().getLanguage().equals("bn");
        if (event.duration > 1) {
            String startFormatted = formatEventDate(event.startDate);
            String endFormatted = formatEventDate(event.endDate);
            String toText = isBangla ? " থেকে " : " to ";
            dateFormatted = startFormatted + toText + endFormatted;
        } else {
            dateFormatted = formatEventDate(event.startDate);
        }
        
        String eventText = "⊙ " + dateFormatted + " - " + event.title;
        holder.tvEventText.setText(eventText);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private String formatEventDate(String dateStr) {
        try {
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat myFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
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
        TextView tvEventText;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventText = itemView.findViewById(R.id.tvEventText);
        }
    }
}
