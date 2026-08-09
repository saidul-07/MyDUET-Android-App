package com.example.myduet;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.myduet.databinding.ActivityEventsBinding;

public class EventsActivity extends AppCompatActivity {

    private ActivityEventsBinding binding;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleHelper.applyLocale(this);
        super.onCreate(savedInstanceState);
        binding = ActivityEventsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(android.graphics.Color.parseColor("#0B3D91"));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility() & ~android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                );
            }
        }

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            appBarConfiguration = new AppBarConfiguration.Builder(new int[]{}).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            binding.toolbar.setNavigationOnClickListener(v -> {
                if (!navController.navigateUp()) {
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}
