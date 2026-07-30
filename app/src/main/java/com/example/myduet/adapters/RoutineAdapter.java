package com.example.myduet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myduet.R;
import com.example.myduet.models.RoutineClass;
import com.google.android.material.chip.Chip;
import java.util.List;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.ViewHolder> {

    private List<RoutineClass> list;

    public RoutineAdapter(List<RoutineClass> list) {
        this.list = list;
    }

    public void updateData(List<RoutineClass> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.routine_class_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoutineClass item = list.get(position);
        holder.tvCode.setText(item.getCourseCode());
        holder.tvName.setText(item.getCourseName());
        holder.tvTime.setText(item.getTime());
        holder.tvRoom.setText(item.getRoom());
        holder.chipType.setText(item.getType());

        holder.viewAccent.setBackgroundResource(R.color.routine_primary);
        if (item.isLab()) {
            holder.chipType.setChipBackgroundColorResource(R.color.routine_lab_bg);
            holder.chipType.setTextColor(holder.itemView.getContext().getColor(R.color.routine_lab_accent));
        } else {
            holder.chipType.setChipBackgroundColorResource(R.color.routine_theory_bg);
            holder.chipType.setTextColor(holder.itemView.getContext().getColor(R.color.routine_theory_accent));
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvName, tvTime, tvRoom;
        Chip chipType;
        View viewAccent;

        ViewHolder(View view) {
            super(view);
            tvCode = view.findViewById(R.id.tvCourseCode);
            tvName = view.findViewById(R.id.tvCourseName);
            tvTime = view.findViewById(R.id.tvTime);
            tvRoom = view.findViewById(R.id.tvRoom);
            chipType = view.findViewById(R.id.chipType);
            viewAccent = view.findViewById(R.id.viewAccent);
        }
    }
}