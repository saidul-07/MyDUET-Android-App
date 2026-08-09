package com.example.myduet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.myduet.databinding.FragmentTeacherDeptBinding;
import com.example.myduet.models.DepartmentInfo;
import com.example.myduet.viewmodels.TeacherViewModel;
import java.util.ArrayList;
import java.util.List;

public class TeacherDeptFragment extends Fragment {

    private FragmentTeacherDeptBinding binding;
    private TeacherViewModel viewModel;
    private DepartmentAdapter adapter;
    private List<DepartmentInfo> allDepartments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherDeptBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(TeacherViewModel.class);

        // Setup 2-Column Grid RecyclerView
        binding.rvDepartments.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new DepartmentAdapter(item -> {
            if (getActivity() instanceof TeachersActivity) {
                ((TeachersActivity) getActivity()).navigateToTeacherList(item.getKey(), item.getName());
            }
        });
        binding.rvDepartments.setAdapter(adapter);

        // Observe departments list
        viewModel.getDepartments().observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                allDepartments = list;
                adapter.setItems(list);
                
                // Calculate dynamic statistics
                int totalTeachers = 0;
                for (DepartmentInfo dept : list) {
                    totalTeachers += dept.getTeacherCount();
                }
                binding.tvTotalTeachersCount.setText(String.valueOf(totalTeachers));
                binding.tvTotalDepartmentsCount.setText(String.valueOf(list.size()));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof TeachersActivity) {
            ((TeachersActivity) getActivity()).updateToolbar("Faculty Directory");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
