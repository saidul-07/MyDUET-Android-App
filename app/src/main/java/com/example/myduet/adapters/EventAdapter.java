package com.example.myduet.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myduet.R;
import com.example.myduet.databinding.ItemEventCardBinding;
import com.example.myduet.models.Event;
import com.example.myduet.viewmodels.EventViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
        void onRegisterClick(Event event);
    }

    private final List<Event> events;
    private final OnEventClickListener listener;

    public EventAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventCardBinding binding = ItemEventCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(events.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        private final ItemEventCardBinding binding;

        EventViewHolder(ItemEventCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event, OnEventClickListener listener) {
            Context context = itemView.getContext();

            // Set Title & Organizer
            binding.tvEventTitle.setText(event.getTitle());
            
            String organizerText = event.getOrganizerName();
            if (event.getClubName() != null && !event.getClubName().trim().isEmpty()) {
                organizerText = event.getClubName();
            }
            binding.tvEventOrganizer.setText(organizerText);

            // Set Venue
            binding.tvEventVenue.setText(event.getVenue());

            // Set Date Info text
            binding.tvEventDate.setText(formatDisplayDate(event.getEventDate()));

            // Set Time Info text
            String timeText = event.getStartTime() + " - " + event.getEndTime();
            binding.tvEventTime.setText(timeText);

            // Set Event Type Badge
            binding.tvEventTypeBadge.setText(event.getType() != null ? event.getType() : "University");

            // Format Left Circular Date Badge (Day + Month)
            setDateBadge(event.getEventDate());

            // Calculate Dynamic Status and style badge
            String status = EventViewModel.calculateEventStatus(event);
            setStatusBadge(status);

            // Set Banner Image
            if (event.getBannerUrl() != null && !event.getBannerUrl().trim().isEmpty()) {
                Glide.with(context)
                        .load(event.getBannerUrl())
                        .placeholder(R.drawable.duet_campus)
                        .error(R.drawable.duet_campus)
                        .into(binding.imgEventBanner);
            } else {
                binding.imgEventBanner.setImageResource(R.drawable.duet_campus);
            }

            // Set Registration Button visibility and state
            setupRegistrationButton(event, listener);

            // Card click navigates to details
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            });
        }

        private void setDateBadge(String dateStr) {
            try {
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = dbFormat.parse(dateStr);
                
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
                
                binding.tvDateDay.setText(dayFormat.format(date));
                binding.tvDateMonth.setText(monthFormat.format(date).toUpperCase());
            } catch (Exception e) {
                binding.tvDateDay.setText("15");
                binding.tvDateMonth.setText("AUG");
            }
        }

        private void setStatusBadge(String status) {
            binding.tvEventStatus.setText(status.toUpperCase());
            
            int bgColor;
            int textColor;

            switch (status) {
                case "Ongoing":
                    bgColor = Color.parseColor("#DCF6F2"); // Light green tint -> Mint green
                    textColor = Color.parseColor("#206E62");
                    binding.tvEventStatus.setText("🟢 ONGOING");
                    break;
                case "Upcoming":
                    bgColor = Color.parseColor("#D7EFF7"); // Light blue tint -> Ocean Blue
                    textColor = Color.parseColor("#044D63");
                    binding.tvEventStatus.setText("🔵 UPCOMING");
                    break;
                case "Completed":
                    bgColor = Color.parseColor("#DCE0EE"); // Light gray tint -> Slate Blue tint
                    textColor = Color.parseColor("#444A72");
                    binding.tvEventStatus.setText("⚪ COMPLETED");
                    break;
                case "Cancelled":
                default:
                    bgColor = Color.parseColor("#DCE0EE"); // Light red tint -> Slate Blue tint
                    textColor = Color.parseColor("#444A72");
                    binding.tvEventStatus.setText("🔴 EVENT CANCELLED");
                    break;
            }

            binding.tvEventStatus.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            binding.tvEventStatus.setTextColor(textColor);
        }

        private void setupRegistrationButton(Event event, OnEventClickListener listener) {
            String eventStatus = EventViewModel.calculateEventStatus(event);
            
            // Check if cancelled
            if ("Cancelled".equals(eventStatus)) {
                binding.btnRegister.setVisibility(View.VISIBLE);
                binding.btnRegister.setEnabled(false);
                binding.btnRegister.setText("Event Cancelled");
                return;
            }

            if (!event.isRegistrationRequired()) {
                // If registration not required, hide the register button on the list
                binding.btnRegister.setVisibility(View.GONE);
                return;
            }

            binding.btnRegister.setVisibility(View.VISIBLE);

            boolean isClosed = EventViewModel.isRegistrationClosed(event);
            if (isClosed) {
                binding.btnRegister.setText("Registration Closed");
                binding.btnRegister.setEnabled(false);
            } else {
                binding.btnRegister.setText("Register Now →");
                binding.btnRegister.setEnabled(true);
                binding.btnRegister.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onRegisterClick(event);
                    }
                });
            }
        }

        private String formatDisplayDate(String dbDate) {
            try {
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = dbFormat.parse(dbDate);
                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                return displayFormat.format(date);
            } catch (Exception e) {
                return dbDate;
            }
        }
    }
}
