package com.example.myduet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.myduet.databinding.FragmentAuthorityLoginBinding;
import com.example.myduet.viewmodels.EventViewModel;

public class AuthorityLoginFragment extends Fragment {

    private FragmentAuthorityLoginBinding binding;
    private EventViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthorityLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        setupTextWatchers();

        binding.btnLogin.setOnClickListener(v -> handleLogin(view));

        binding.tvStudentView.setOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp();
        });
    }

    private void handleLogin(View view) {
        String userId = binding.etUserId.getText() != null ? binding.etUserId.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";

        boolean isValid = true;
        binding.tilUserId.setError(null);
        binding.tilPassword.setError(null);

        if (userId.isEmpty()) {
            binding.tilUserId.setError("User ID is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError("Password is required");
            isValid = false;
        }

        if (!isValid) return;

        // Perform login
        boolean success = viewModel.login(userId, password);
        if (success) {
            Toast.makeText(getContext(), "Authentication Successful!", Toast.LENGTH_SHORT).show();
            // Redirect to dashboard
            Navigation.findNavController(view).navigate(
                    R.id.action_authorityLoginFragment_to_authorityDashboardFragment
            );
        } else {
            Toast.makeText(getContext(), "Invalid User ID or Password", Toast.LENGTH_LONG).show();
        }
    }

    private void setupTextWatchers() {
        binding.etUserId.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilUserId.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilPassword.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
