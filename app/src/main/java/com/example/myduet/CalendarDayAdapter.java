package com.example.myduet;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CalendarDayAdapter extends RecyclerView.Adapter<CalendarDayAdapter.DayViewHolder> {

    public interface OnDayClickListener {
        void onDayClick(int day);
    }

    private final List<CalendarDay> dayList;
    private final OnDayClickListener listener;

    public CalendarDayAdapter(List<CalendarDay> dayList, OnDayClickListener listener) {
        this.dayList = dayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        CalendarDay cell = dayList.get(position);
        
        if (cell.dayNumber == 0) {
            holder.tvDayNumber.setText("");
            holder.vSelectedBg.setVisibility(View.GONE);
            holder.vHolidayBg.setVisibility(View.GONE);
            holder.vHolidayDot.setVisibility(View.GONE);
            holder.itemView.setClickable(false);
        } else {
            holder.tvDayNumber.setText(String.format(java.util.Locale.getDefault(), "%d", cell.dayNumber));
            holder.itemView.setClickable(true);
            
            int dayOfWeek = position % 7;
            
            if (cell.isSelected) {
                holder.vSelectedBg.setVisibility(View.VISIBLE);
                holder.vHolidayBg.setVisibility(View.GONE);
                holder.tvDayNumber.setTextColor(Color.WHITE);
            } else if (cell.isHoliday) {
                holder.vSelectedBg.setVisibility(View.GONE);
                holder.vHolidayBg.setVisibility(View.VISIBLE);
                holder.tvDayNumber.setTextColor(Color.WHITE);
            } else {
                holder.vSelectedBg.setVisibility(View.GONE);
                holder.vHolidayBg.setVisibility(View.GONE);
                if (dayOfWeek == 5 || dayOfWeek == 6) {
                    holder.tvDayNumber.setTextColor(Color.parseColor("#E53935")); // Red for Fri/Sat
                } else {
                    holder.tvDayNumber.setTextColor(androidx.core.content.ContextCompat.getColor(holder.itemView.getContext(), R.color.calendar_day_text));
                }
            }
            
            holder.vHolidayDot.setVisibility(View.GONE); // No dot, we use red background circle
            
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDayClick(cell.dayNumber);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    public static class CalendarDay {
        public int dayNumber;
        public boolean isSelected;
        public boolean isHoliday;

        public CalendarDay(int dayNumber, boolean isSelected, boolean isHoliday) {
            this.dayNumber = dayNumber;
            this.isSelected = isSelected;
            this.isHoliday = isHoliday;
        }
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        View vSelectedBg;
        View vHolidayBg;
        TextView tvDayNumber;
        View vHolidayDot;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            vSelectedBg = itemView.findViewById(R.id.vSelectedBg);
            vHolidayBg = itemView.findViewById(R.id.vHolidayBg);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
            vHolidayDot = itemView.findViewById(R.id.vHolidayDot);
        }
    }
}
