package com.example.myduet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myduet.storage.AuthManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private boolean isLoginMode = true; // Toggle between Login and Register modes
    private AuthManager authManager;

    private TextView tvLoginTitle, tvLoginSubtitle, tvForgotPassword, tvToggleMode;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager(this);

        // Bind Views
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        tvLoginSubtitle = findViewById(R.id.tvLoginSubtitle);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvToggleMode = findViewById(R.id.tvToggleMode);

        // Clear errors as user types
        setupTextWatchers();

        // Mode toggle click listener
        tvToggleMode.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            updateUI();
        });

        // Submit button click listener
        btnLogin.setOnClickListener(v -> handleAuthentication());

        // Forgot password handler
        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        // Initial UI state
        updateUI();
    }

    private void updateUI() {
        // Clear inputs and errors when switching modes
        etEmail.setText("");
        etPassword.setText("");
        tilEmail.setError(null);
        tilPassword.setError(null);

        if (isLoginMode) {
            tvLoginTitle.setText("Welcome Back");
            tvLoginSubtitle.setText("Sign in to your account to continue");
            btnLogin.setText("Login");
            tvForgotPassword.setVisibility(View.VISIBLE);
            tvToggleMode.setText("Don't have an account? Register");
        } else {
            tvLoginTitle.setText("Create Account");
            tvLoginSubtitle.setText("Sign up with your Gmail or DUET Edu mail");
            btnLogin.setText("Register");
            tvForgotPassword.setVisibility(View.GONE);
            tvToggleMode.setText("Already have an account? Login");
        }
    }

    private void handleAuthentication() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

        // Reset errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        boolean isValid = true;

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!AuthManager.isValidEmail(email)) {
            tilEmail.setError("Please enter a valid Gmail or Edu Mail (e.g. name@gmail.com, name@duet.ac.bd)");
            isValid = false;
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Password must be at least 6 characters");
            isValid = false;
        }

        if (!isValid) return;

        if (isLoginMode) {
            // Login Mode
            if (authManager.verifyCredentials(email, password)) {
                authManager.setLoggedIn(email, true);
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Register Mode
            if (authManager.registerUser(email, password)) {
                Toast.makeText(this, "Registration Successful! Please login.", Toast.LENGTH_LONG).show();
                isLoginMode = true;
                updateUI();
            } else {
                tilEmail.setError("This email is already registered");
            }
        }
    }

    private void setupTextWatchers() {
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void showForgotPasswordDialog() {
        final TextInputLayout tilEmailInput = new TextInputLayout(this);
        tilEmailInput.setHint("Email Address");
        
        // Cast View to TextInputLayout before calling getStartIconDrawable()
        TextInputLayout emailLayout = findViewById(R.id.tilEmail);
        if (emailLayout != null) {
            tilEmailInput.setStartIconDrawable(emailLayout.getStartIconDrawable());
        }
        tilEmailInput.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        
        final TextInputEditText etEmailInput = new TextInputEditText(tilEmailInput.getContext());
        etEmailInput.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        tilEmailInput.addView(etEmailInput);

        if (etEmail.getText() != null) {
            etEmailInput.setText(etEmail.getText().toString());
        }

        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (24 * getResources().getDisplayMetrics().density);
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.topMargin = (int) (12 * getResources().getDisplayMetrics().density);
        params.bottomMargin = (int) (12 * getResources().getDisplayMetrics().density);
        tilEmailInput.setLayoutParams(params);
        container.addView(tilEmailInput);

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("Enter your registered Gmail or Edu mail address:")
            .setView(container)
            .setPositiveButton("Verify", (dialog, which) -> {
                String email = etEmailInput.getText() != null ? etEmailInput.getText().toString().trim() : "";
                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!AuthManager.isValidEmail(email)) {
                    Toast.makeText(LoginActivity.this, "Invalid email domain format", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (authManager.isUserRegistered(email)) {
                    showNewPasswordDialog(email);
                } else {
                    Toast.makeText(LoginActivity.this, "Email is not registered!", Toast.LENGTH_LONG).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showNewPasswordDialog(String email) {
        final TextInputLayout tilPasswordInput = new TextInputLayout(this);
        tilPasswordInput.setHint("New Password");
        
        // Cast View to TextInputLayout before calling getStartIconDrawable()
        TextInputLayout passwordLayout = findViewById(R.id.tilPassword);
        if (passwordLayout != null) {
            tilPasswordInput.setStartIconDrawable(passwordLayout.getStartIconDrawable());
        }
        tilPasswordInput.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        tilPasswordInput.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        
        final TextInputEditText etPasswordInput = new TextInputEditText(tilPasswordInput.getContext());
        etPasswordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        tilPasswordInput.addView(etPasswordInput);

        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (24 * getResources().getDisplayMetrics().density);
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.topMargin = (int) (12 * getResources().getDisplayMetrics().density);
        params.bottomMargin = (int) (12 * getResources().getDisplayMetrics().density);
        tilPasswordInput.setLayoutParams(params);
        container.addView(tilPasswordInput);

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Set New Password")
            .setMessage("Enter new password for: " + email)
            .setView(container)
            .setPositiveButton("Reset", (dialog, which) -> {
                String newPassword = etPasswordInput.getText() != null ? etPasswordInput.getText().toString() : "";
                if (newPassword.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.length() < 6) {
                    Toast.makeText(LoginActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (authManager.resetPassword(email, newPassword)) {
                    Toast.makeText(LoginActivity.this, "Password reset successfully! Please login.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error resetting password. Please try again.", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}