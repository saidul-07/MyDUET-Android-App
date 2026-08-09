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
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myduet.databinding.FragmentTeacherListBinding;
import com.example.myduet.models.Teacher;
import com.example.myduet.viewmodels.TeacherViewModel;

public class TeacherListFragment extends Fragment {

    private FragmentTeacherListBinding binding;
    private TeacherViewModel viewModel;
    private TeacherAdapter adapter;
    private String deptKey;
    private String deptName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            deptKey = getArguments().getString("dept_key");
            deptName = getArguments().getString("dept_name");
        }

        viewModel = new ViewModelProvider(requireActivity()).get(TeacherViewModel.class);

        // Load teachers for selected department
        viewModel.selectDepartment(deptKey);

        // Setup RecyclerView
        binding.rvTeachers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeacherAdapter(item -> {
            if (getActivity() instanceof TeachersActivity) {
                ((TeachersActivity) getActivity()).navigateToTeacherProfile(item);
            }
        });
        binding.rvTeachers.setAdapter(adapter);

        // Setup TextWatcher for search
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
                binding.btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnClearSearch.setOnClickListener(v -> binding.etSearch.setText(""));

        // Setup Chips filtering
        binding.chipGroupDesignation.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                viewModel.setDesignationFilter("All");
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipAll) {
                viewModel.setDesignationFilter("All");
            } else if (checkedId == R.id.chipProfessor) {
                viewModel.setDesignationFilter("Professor");
            } else if (checkedId == R.id.chipAssociate) {
                viewModel.setDesignationFilter("Associate Professor");
            } else if (checkedId == R.id.chipAssistant) {
                viewModel.setDesignationFilter("Assistant Professor");
            } else if (checkedId == R.id.chipLecturer) {
                viewModel.setDesignationFilter("Lecturer");
            }
        });

        // Observe teachers list
        viewModel.getTeachers().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                binding.rvTeachers.setVisibility(View.GONE);
                binding.layoutEmptyState.setVisibility(View.VISIBLE);
            } else {
                binding.rvTeachers.setVisibility(View.VISIBLE);
                binding.layoutEmptyState.setVisibility(View.GONE);
                adapter.setItems(list);
            }
        });

        // Clear query and filter chips state on initialization
        viewModel.setSearchQuery("");
        viewModel.setDesignationFilter("All");
        binding.chipAll.setChecked(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof TeachersActivity) {
            String title = (deptName != null ? deptName : "Teachers");
            if (title.length() > 25) {
                // Shorten long department names for mobile layout toolbar safety
                title = deptKey.toUpperCase() + " Teachers";
            }
            ((TeachersActivity) getActivity()).updateToolbar(title);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
