package com.example.myduet;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.myduet.databinding.FragmentEventDetailBinding;
import com.example.myduet.models.Event;
import com.example.myduet.viewmodels.EventViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetailFragment extends Fragment {

    private FragmentEventDetailBinding binding;
    private Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            event = (Event) getArguments().getSerializable("event");
        }

        if (event == null) {
            Toast.makeText(getContext(), "Error loading event details", Toast.LENGTH_SHORT).show();
            if (getActivity() != null) getActivity().onBackPressed();
            return;
        }

        bindEventDetails();
    }

    private void bindEventDetails() {
        // Set Banner
        if (event.getBannerUrl() != null && !event.getBannerUrl().trim().isEmpty()) {
            Glide.with(this)
                    .load(event.getBannerUrl())
                    .placeholder(R.drawable.duet_campus)
                    .error(R.drawable.duet_campus)
                    .into(binding.imgDetailBanner);
        } else {
            binding.imgDetailBanner.setImageResource(R.drawable.duet_campus);
        }

        // Set Title & Organizer
        binding.tvDetailTitle.setText(event.getTitle());
        String organizer = event.getOrganizerName();
        if (event.getClubName() != null && !event.getClubName().trim().isEmpty()) {
            organizer = event.getClubName();
        }
        binding.tvDetailOrganizer.setText("Organized by: " + organizer);

        // Set Description
        binding.tvDetailDescription.setText(event.getDescription());

        // Set Date
        binding.tvDetailDate.setText("Date: " + formatDisplayDate(event.getEventDate()));

        // Set Time
        binding.tvDetailTime.setText("Time: " + event.getStartTime() + " - " + event.getEndTime());

        // Set Venue
        binding.tvDetailVenue.setText("Venue: " + event.getVenue());

        // Set Type
        binding.tvDetailType.setText(event.getType() + " Event");

        // Calculate Status and style badge
        String status = EventViewModel.calculateEventStatus(event);
        setStatusBadge(status);

        // Deadline info
        if (event.isRegistrationRequired() && event.getRegistrationDeadline() != null && !event.getRegistrationDeadline().trim().isEmpty()) {
            binding.layoutDeadlineInfo.setVisibility(View.VISIBLE);
            binding.tvDetailDeadline.setText("Registration Deadline: " + formatDisplayDeadline(event.getRegistrationDeadline()));
        } else {
            binding.layoutDeadlineInfo.setVisibility(View.GONE);
        }

        // Optional Fields
        boolean hasOptional = false;
        StringBuilder sb = new StringBuilder();
        
        if (event.getMaxParticipants() != null && event.getMaxParticipants() > 0) {
            binding.tvDetailMaxParticipants.setVisibility(View.VISIBLE);
            binding.tvDetailMaxParticipants.setText("Maximum Participants: " + event.getMaxParticipants());
            hasOptional = true;
        } else {
            binding.tvDetailMaxParticipants.setVisibility(View.GONE);
        }

        if (event.getSocialMediaUrl() != null && !event.getSocialMediaUrl().trim().isEmpty()) {
            binding.tvDetailSocialMedia.setVisibility(View.VISIBLE);
            binding.tvDetailSocialMedia.setText("Social Media: " + event.getSocialMediaUrl());
            hasOptional = true;
        } else {
            binding.tvDetailSocialMedia.setVisibility(View.GONE);
        }

        if (event.getAdditionalInfo() != null && !event.getAdditionalInfo().trim().isEmpty()) {
            binding.tvDetailAdditional.setVisibility(View.VISIBLE);
            binding.tvDetailAdditional.setText("Additional Info: " + event.getAdditionalInfo());
            hasOptional = true;
        } else {
            binding.tvDetailAdditional.setVisibility(View.GONE);
        }

        binding.layoutOptionalInfo.setVisibility(hasOptional ? View.VISIBLE : View.GONE);

        // Contact info
        binding.tvContactName.setText(event.getContactName());
        binding.btnContactCall.setOnClickListener(v -> {
            if (event.getContactPhone() != null && !event.getContactPhone().trim().isEmpty()) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + event.getContactPhone().trim()));
                startActivity(dialIntent);
            } else {
                Toast.makeText(getContext(), "No phone number available", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnContactEmail.setOnClickListener(v -> {
            if (event.getContactEmail() != null && !event.getContactEmail().trim().isEmpty()) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + event.getContactEmail().trim()));
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            } else {
                Toast.makeText(getContext(), "No email address available", Toast.LENGTH_SHORT).show();
            }
        });

        // Bottom Registration Sticky Button Configuration
        setupRegistrationButton(status);
    }

    private void setStatusBadge(String status) {
        binding.tvDetailStatus.setText(status.toUpperCase());
        int bgColor;
        int textColor;

        switch (status) {
            case "Ongoing":
                bgColor = Color.parseColor("#E8F5E9");
                textColor = Color.parseColor("#2E7D32");
                binding.tvDetailStatus.setText("🟢 ONGOING");
                break;
            case "Upcoming":
                bgColor = Color.parseColor("#E3F2FD");
                textColor = Color.parseColor("#1565C0");
                binding.tvDetailStatus.setText("🔵 UPCOMING");
                break;
            case "Completed":
                bgColor = Color.parseColor("#F5F5F5");
                textColor = Color.parseColor("#616161");
                binding.tvDetailStatus.setText("⚪ COMPLETED");
                break;
            case "Cancelled":
            default:
                bgColor = Color.parseColor("#FFEBEE");
                textColor = Color.parseColor("#C62828");
                binding.tvDetailStatus.setText("🔴 EVENT CANCELLED");
                break;
        }

        binding.tvDetailStatus.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        binding.tvDetailStatus.setTextColor(textColor);
    }

    private void setupRegistrationButton(String eventStatus) {
        if ("Cancelled".equals(eventStatus)) {
            binding.btnDetailRegister.setVisibility(View.VISIBLE);
            binding.btnDetailRegister.setEnabled(false);
            binding.btnDetailRegister.setText("Event Cancelled");
            binding.btnDetailRegister.setBackgroundColor(Color.parseColor("#C62828"));
            return;
        }

        if (!event.isRegistrationRequired()) {
            binding.btnDetailRegister.setVisibility(View.GONE);
            return;
        }

        binding.btnDetailRegister.setVisibility(View.VISIBLE);

        boolean isClosed = EventViewModel.isRegistrationClosed(event);
        if (isClosed) {
            binding.btnDetailRegister.setText("Registration Closed");
            binding.btnDetailRegister.setEnabled(false);
            binding.btnDetailRegister.setBackgroundColor(Color.parseColor("#B0B0B0"));
        } else {
            binding.btnDetailRegister.setText("Register Now →");
            binding.btnDetailRegister.setEnabled(true);
            binding.btnDetailRegister.setOnClickListener(v -> {
                if (event.getRegistrationUrl() != null && !event.getRegistrationUrl().trim().isEmpty()) {
                    try {
                        Uri uri = Uri.parse(event.getRegistrationUrl());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Invalid link", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private String formatDisplayDate(String dbDate) {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = dbFormat.parse(dbDate);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
            return displayFormat.format(date);
        } catch (Exception e) {
            return dbDate;
        }
    }

    private String formatDisplayDeadline(String dbDeadline) {
        try {
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            Date date = dbFormat.parse(dbDeadline);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
            return displayFormat.format(date);
        } catch (Exception e) {
            return dbDeadline;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
