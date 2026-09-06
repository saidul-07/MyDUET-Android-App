package com.example.myduet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

        binding.btnSearch.setOnClickListener(v -> performSearch());

        binding.etRollNumber.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch();
                return true;
            }
            return false;
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.layoutResultContainer.setVisibility(View.GONE);
                binding.layoutError.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getSearchResult().observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.layoutError.setVisibility(View.GONE);
                binding.layoutResultContainer.setVisibility(View.VISIBLE);
                displayResult(result);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.layoutResultContainer.setVisibility(View.GONE);
                binding.layoutError.setVisibility(View.VISIBLE);
                if (error.equalsIgnoreCase("Result Not Found")) {
                    binding.tvErrorTitle.setText("No Admission Result Found");
                    binding.tvErrorSubtitle.setText("Please check the admission roll number and try again.");
                } else {
                    binding.tvErrorTitle.setText("No Admission Result Found");
                    binding.tvErrorSubtitle.setText(error);
                }
            }
        });
    }

    private void performSearch() {
        hideKeyboard();
        binding.layoutError.setVisibility(View.GONE);
        String roll = "";
        if (binding.etRollNumber.getText() != null) {
            roll = binding.etRollNumber.getText().toString().trim();
        }
        viewModel.search(roll);
    }

    private void hideKeyboard() {
        if (getActivity() != null && getView() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }

    private void displayResult(AdmissionResult result) {
        if (result.getName() != null && !result.getName().trim().isEmpty()) {
            binding.tvName.setText(result.getName());
            binding.tvName.setVisibility(View.VISIBLE);
        } else {
            binding.tvName.setText("Candidate Result");
        }

        binding.tvRoll.setText("Admission Roll  •  " + result.getRoll());

        if (result.getFatherName() != null && !result.getFatherName().trim().isEmpty()) {
            binding.tvFatherName.setText(result.getFatherName());
            binding.rowFatherName.setVisibility(View.VISIBLE);
            binding.divFather.setVisibility(View.VISIBLE);
        } else {
            binding.rowFatherName.setVisibility(View.GONE);
            binding.divFather.setVisibility(View.GONE);
        }

        binding.tvDept.setText(result.getDepartment() != null ? result.getDepartment() : "N/A");

        if ("Selected".equalsIgnoreCase(result.getStatus())) {
            binding.tvStatus.setText("SELECTED");
            binding.cardStatusBadge.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary_light));
            binding.ivStatusIcon.setImageResource(R.drawable.ic_check);
            binding.ivStatusIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary_darker)));
            binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_darker));
            
            binding.rowWaitingMerit.setVisibility(View.GONE);
            binding.divWaitingMerit.setVisibility(View.GONE);
        } else {
            String status = (result.getStatus() != null && !result.getStatus().trim().isEmpty()) ? result.getStatus().toUpperCase() : "WAITING";
            binding.tvStatus.setText(status);
            binding.cardStatusBadge.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_orange));
            binding.ivStatusIcon.setImageResource(R.drawable.ic_time);
            binding.ivStatusIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary_dark)));
            binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_dark));

            if (result.getWaitingMerit() != null) {
                binding.tvWaitingMerit.setText(String.valueOf(result.getWaitingMerit()));
                binding.rowWaitingMerit.setVisibility(View.VISIBLE);
                binding.divWaitingMerit.setVisibility(View.VISIBLE);
            } else {
                binding.rowWaitingMerit.setVisibility(View.GONE);
                binding.divWaitingMerit.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}