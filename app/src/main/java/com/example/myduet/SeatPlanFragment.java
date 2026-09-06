package com.example.myduet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.myduet.databinding.FragmentSeatPlanBinding;
import com.example.myduet.models.SeatPlan;
import com.example.myduet.viewmodels.SeatPlanViewModel;

public class SeatPlanFragment extends Fragment {

    private FragmentSeatPlanBinding binding;
    private SeatPlanViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSeatPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SeatPlanViewModel.class);

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
                binding.cardResult.setVisibility(View.GONE);
                binding.layoutError.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getSeatPlanResult().observe(getViewLifecycleOwner(), seatPlan -> {
            if (seatPlan != null) {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.layoutError.setVisibility(View.GONE);
                binding.cardResult.setVisibility(View.VISIBLE);
                displaySeatPlan(seatPlan);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.cardResult.setVisibility(View.GONE);
                binding.layoutError.setVisibility(View.VISIBLE);
                if (error.equalsIgnoreCase("Seat Plan Not Found")) {
                    binding.tvErrorTitle.setText("No Seat Plan Found");
                    binding.tvErrorSubtitle.setText("Please check the roll number and try again.");
                } else {
                    binding.tvErrorTitle.setText("No Seat Plan Found");
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

    private void displaySeatPlan(SeatPlan seatPlan) {
        if (seatPlan.getCandidateName() != null && !seatPlan.getCandidateName().trim().isEmpty()) {
            binding.tvName.setText(seatPlan.getCandidateName());
        } else {
            binding.tvName.setText("Seat Plan Details");
        }

        binding.tvRoll.setText("Roll: " + seatPlan.getSearchedRoll());

        if (seatPlan.getCandidateFatherName() != null && !seatPlan.getCandidateFatherName().trim().isEmpty()) {
            binding.tvFatherName.setText(seatPlan.getCandidateFatherName());
            binding.rowFatherName.setVisibility(View.VISIBLE);
        } else {
            binding.rowFatherName.setVisibility(View.GONE);
        }

        binding.tvDepartment.setText(seatPlan.getDepartment() != null ? seatPlan.getDepartment() : "N/A");
        binding.tvBuilding.setText(seatPlan.getBuilding() != null ? seatPlan.getBuilding() : "N/A");
        binding.tvRoom.setText(seatPlan.getRoom() != null ? "Room: " + seatPlan.getRoom() : "N/A");
        binding.tvExamDate.setText(seatPlan.getExamDate() != null ? seatPlan.getExamDate() : "N/A");
        binding.tvShift.setText(seatPlan.getShift() != null ? seatPlan.getShift() : "N/A");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}