package com.example.myduet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        viewModel.getSeatPlanResult().observe(getViewLifecycleOwner(), seatPlan -> {
            if (seatPlan != null) {
                binding.cardResult.setVisibility(View.VISIBLE);
                binding.tvError.setVisibility(View.GONE);
                displaySeatPlan(seatPlan);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.tvError.setVisibility(View.VISIBLE);
                binding.tvError.setText(error);
                binding.cardResult.setVisibility(View.GONE);
            }
        });
    }

    private void displaySeatPlan(SeatPlan seatPlan) {
        binding.tvName.setText("Seat Plan Details");
        binding.tvRoll.setText("Admission Roll: " + binding.etRollNumber.getText().toString());
        
        binding.rowCenter.tvLabel.setText("Department");
        binding.rowCenter.tvValue.setText(seatPlan.getDepartment());

        binding.rowBuilding.tvLabel.setText("Building");
        binding.rowBuilding.tvValue.setText(seatPlan.getBuilding());

        binding.rowRoom.tvLabel.setText("Room / Details");
        binding.rowRoom.tvValue.setText(seatPlan.getRoom());

        binding.rowSeat.tvLabel.setText("Exam Date");
        binding.rowSeat.tvValue.setText(seatPlan.getExamDate());

        binding.rowTime.tvLabel.setText("Exam Shift");
        binding.rowTime.tvValue.setText(seatPlan.getShift());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}