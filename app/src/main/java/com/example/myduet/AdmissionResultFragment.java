package com.example.myduet;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myduet.databinding.FragmentAdmissionResultBinding;
import com.example.myduet.models.AdmissionResult;
import com.example.myduet.viewmodels.AdmissionResultViewModel;

public class AdmissionResultFragment extends Fragment {

    private FragmentAdmissionResultBinding binding;
    private AdmissionResultViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdmissionResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AdmissionResultViewModel.class);

        binding.btnSearch.setOnClickListener(v -> {
            String roll = binding.etRollNumber.getText().toString();
            viewModel.search(roll);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                binding.cardResult.setVisibility(View.GONE);
                binding.tvError.setVisibility(View.GONE);
            }
        });

        viewModel.getSearchResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                binding.cardResult.setVisibility(View.VISIBLE);
                binding.tvError.setVisibility(View.GONE);
                displayResult(result);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.tvError.setVisibility(View.VISIBLE);
                binding.tvError.setText(error);
                binding.cardResult.setVisibility(View.GONE);
            }
        });
    }

    private void displayResult(AdmissionResult result) {
        if (result.getName() != null && !result.getName().trim().isEmpty()) {
            binding.tvName.setText(result.getName());
            binding.tvName.setVisibility(View.VISIBLE);
        } else {
            binding.tvName.setVisibility(View.GONE);
        }
        binding.tvRoll.setText("Roll: " + result.getRoll());

        binding.rowFatherName.tvLabel.setText("Father's Name");
        binding.rowFatherName.tvValue.setText(result.getFatherName());
        binding.rowFatherName.getRoot().setVisibility(View.VISIBLE);

        binding.rowDept.tvLabel.setText("Department");
        binding.rowDept.tvValue.setText(result.getDepartment());
        binding.rowDept.getRoot().setVisibility(View.VISIBLE);

        binding.chipStatus.setText(result.getStatus());
        
        if ("Selected".equalsIgnoreCase(result.getStatus())) {
            binding.chipStatus.setChipIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_check));
            binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.routine_lab_bg)));
            binding.chipStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.routine_lab_accent));
            binding.rowWaitingMerit.getRoot().setVisibility(View.GONE);
        } else {
            binding.chipStatus.setChipIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_refresh));
            binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.admission_bg)));
            binding.chipStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.admission_accent));
            
            if (result.getWaitingMerit() != null) {
                binding.rowWaitingMerit.tvLabel.setText("Waiting Merit");
                binding.rowWaitingMerit.tvValue.setText(String.valueOf(result.getWaitingMerit()));
                binding.rowWaitingMerit.getRoot().setVisibility(View.VISIBLE);
            } else {
                binding.rowWaitingMerit.getRoot().setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}