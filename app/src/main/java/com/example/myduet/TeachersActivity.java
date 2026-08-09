package com.example.myduet;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.myduet.databinding.ActivityTeachersBinding;
import com.example.myduet.models.Teacher;
import com.google.gson.Gson;

public class TeachersActivity extends AppCompatActivity {

    private ActivityTeachersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeachersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar back navigation
        binding.toolbarTeachers.setNavigationOnClickListener(v -> onBackPressed());
        LocaleHelper.styleAppBar(this, binding.toolbarTeachers, "#005FB0", "#004F90");

        // Show default fragment (Directory Home)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.teachers_container, new DirectoryHomeFragment())
                    .commit();
        }
    }

    public void navigateToDeptList() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.teachers_container, new TeacherDeptFragment())
                .addToBackStack(null)
                .commit();
    }

    public void updateToolbar(String title) {
        binding.toolbarTeachers.setTitle(title);
    }

    public void navigateToTeacherList(String deptKey, String deptName) {
        TeacherListFragment fragment = new TeacherListFragment();
        Bundle args = new Bundle();
        args.putString("dept_key", deptKey);
        args.putString("dept_name", deptName);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.teachers_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void navigateToTeacherProfile(Teacher teacher) {
        TeacherProfileFragment fragment = new TeacherProfileFragment();
        Bundle args = new Bundle();
        // Pass teacher object as JSON string to easily reconstruct in fragment
        args.putString("teacher_json", new Gson().toJson(teacher));
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.teachers_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
