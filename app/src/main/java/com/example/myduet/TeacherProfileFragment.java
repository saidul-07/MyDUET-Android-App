package com.example.myduet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.myduet.databinding.FragmentTeacherProfileBinding;
import com.example.myduet.models.Teacher;
import com.google.gson.Gson;

public class TeacherProfileFragment extends Fragment {

    private FragmentTeacherProfileBinding binding;
    private Teacher teacher;
    private final int[] avatarColors = {
        R.color.accent_blue, R.color.accent_purple, R.color.accent_red,
        R.color.accent_green, R.color.accent_orange, R.color.accent_teal,
        R.color.pgr_bg, R.color.club_bg, R.color.admission_bg
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String jsonStr = getArguments().getString("teacher_json");
            teacher = new Gson().fromJson(jsonStr, Teacher.class);
        }

        if (teacher != null) {
            populateProfile();
        }
    }

    private void populateProfile() {
        binding.tvProfileName.setText(teacher.getName());
        binding.tvProfileDesignation.setText(teacher.getDesignation());
        binding.tvProfileEmail.setText(teacher.getEmail());
        binding.tvProfilePhone.setText(teacher.getPhone() != null ? teacher.getPhone() : "Not Available");
        binding.tvProfileOffice.setText(teacher.getOfficeRoom());
        binding.tvProfileResearch.setText(teacher.getResearchInterests() != null ? teacher.getResearchInterests() : "General Engineering Research");

        // Format initials and background color to match the list view
        String initials = getInitials(teacher.getName());
        binding.tvProfileInitials.setText(initials);
        
        int colorIndex = Math.abs(teacher.getName().hashCode()) % avatarColors.length;
        int color = ContextCompat.getColor(requireContext(), avatarColors[colorIndex]);
        binding.cardLargeAvatar.setCardBackgroundColor(color);

        if (teacher.getImage() != null && !teacher.getImage().isEmpty()) {
            binding.imgProfilePhoto.setVisibility(View.VISIBLE);
            com.bumptech.glide.Glide.with(this)
                    .load(teacher.getImage())
                    .into(binding.imgProfilePhoto);
        } else {
            binding.imgProfilePhoto.setVisibility(View.GONE);
        }

        // Department display
        String deptDisplay = "DUET Faculty";
        if (teacher.getEmail().contains(".cse")) {
            deptDisplay = "Computer Science & Engineering";
        } else if (teacher.getEmail().contains(".eee")) {
            deptDisplay = "Electrical & Electronic Engineering";
        } else if (teacher.getEmail().contains(".ce")) {
            deptDisplay = "Civil Engineering";
        } else if (teacher.getEmail().contains(".me")) {
            deptDisplay = "Mechanical Engineering";
        } else if (teacher.getEmail().contains(".te")) {
            deptDisplay = "Textile Engineering";
        } else if (teacher.getEmail().contains(".ipe")) {
            deptDisplay = "Industrial & Production Engineering";
        } else if (teacher.getEmail().contains(".arch")) {
            deptDisplay = "Architecture";
        } else if (teacher.getEmail().contains(".fe")) {
            deptDisplay = "Food Engineering";
        } else if (teacher.getEmail().contains(".che")) {
            deptDisplay = "Chemical Engineering";
        }
        binding.tvProfileDepartment.setText(deptDisplay);

        // Setup Quick Actions
        binding.btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + teacher.getEmail()));
            try {
                startActivity(Intent.createChooser(intent, "Send Email via..."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.btnProfile.setOnClickListener(v -> {
            String url = teacher.getProfile();
            if (url != null && !url.isEmpty()) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", url);
                intent.putExtra("title", teacher.getName() + " - Profile");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "?";

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

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof TeachersActivity) {
            ((TeachersActivity) getActivity()).updateToolbar("Teacher Profile");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
