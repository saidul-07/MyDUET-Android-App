package com.example.myduet.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myduet.R;
import com.example.myduet.models.DepartmentInfo;
import com.example.myduet.models.Teacher;
import com.example.myduet.repositories.TeacherRepository;
import java.util.ArrayList;
import java.util.List;

public class TeacherViewModel extends AndroidViewModel {

    private final TeacherRepository repository;
    private final MutableLiveData<List<DepartmentInfo>> departments = new MutableLiveData<>();
    private final MutableLiveData<List<Teacher>> teachers = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<String> designationFilter = new MutableLiveData<>("All");
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final List<DepartmentInfo> departmentList = new ArrayList<>();
    private List<Teacher> fullTeacherList = new ArrayList<>();

    public TeacherViewModel(@NonNull Application application) {
        super(application);
        repository = new TeacherRepository(application);
        initializeDepartments();
    }

    private void initializeDepartments() {
        departmentList.add(new DepartmentInfo("cse", "Computer Science & Engineering", R.color.accent_blue, R.drawable.ic_profile, 0));
        departmentList.add(new DepartmentInfo("eee", "Electrical & Electronic Engineering", R.color.accent_purple, R.drawable.ic_services, 0));
        departmentList.add(new DepartmentInfo("ce", "Civil Engineering", R.color.accent_orange, R.drawable.ic_library, 0));
        departmentList.add(new DepartmentInfo("me", "Mechanical Engineering", R.color.accent_red, R.drawable.ic_emergency, 0));
        departmentList.add(new DepartmentInfo("te", "Textile Engineering", R.color.accent_teal, R.drawable.ic_transport, 0));
        departmentList.add(new DepartmentInfo("ipe", "Industrial & Production Engineering", R.color.accent_green, R.drawable.ic_school, 0));
        departmentList.add(new DepartmentInfo("arch", "Architecture", R.color.pgr_bg, R.drawable.ic_book, 0));
        departmentList.add(new DepartmentInfo("fe", "Food Engineering", R.color.club_bg, R.drawable.ic_calendar_today, 0));
        departmentList.add(new DepartmentInfo("che", "Chemical Engineering", R.color.admission_bg, R.drawable.ic_school, 0));
        departmentList.add(new DepartmentInfo("math", "Mathematics", R.color.accent_blue, R.drawable.ic_book, 0));
        departmentList.add(new DepartmentInfo("chem", "Chemistry", R.color.accent_purple, R.drawable.ic_book, 0));
        departmentList.add(new DepartmentInfo("phy", "Physics", R.color.accent_teal, R.drawable.ic_book, 0));
        departmentList.add(new DepartmentInfo("hss", "Humanities & Social Sciences", R.color.accent_orange, R.drawable.ic_book, 0));

        // Load teacher counts
        for (DepartmentInfo dept : departmentList) {
            List<Teacher> list = repository.loadTeachers(dept.getKey());
            dept.setTeacherCount(list.size());
        }
        departments.setValue(departmentList);
    }

    public LiveData<List<DepartmentInfo>> getDepartments() {
        return departments;
    }

    public LiveData<List<Teacher>> getTeachers() {
        return teachers;
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public LiveData<String> getDesignationFilter() {
        return designationFilter;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void selectDepartment(String deptKey) {
        isLoading.setValue(true);
        fullTeacherList = repository.loadTeachers(deptKey);
        applyFilters();
        isLoading.setValue(false);
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        applyFilters();
    }

    public void setDesignationFilter(String filter) {
        designationFilter.setValue(filter);
        applyFilters();
    }

    private void applyFilters() {
        String query = searchQuery.getValue() == null ? "" : searchQuery.getValue().toLowerCase().trim();
        String filter = designationFilter.getValue() == null ? "All" : designationFilter.getValue();

        List<Teacher> filtered = new ArrayList<>();
        for (Teacher t : fullTeacherList) {
            // designation filter check
            if (!filter.equals("All")) {
                if (!t.getDesignation().equalsIgnoreCase(filter)) {
                    continue;
                }
            }

            // search query check
            if (!query.isEmpty()) {
                boolean matchesName = t.getName().toLowerCase().contains(query);
                boolean matchesDesignation = t.getDesignation().toLowerCase().contains(query);
                if (!matchesName && !matchesDesignation) {
                    continue;
                }
            }

            filtered.add(t);
        }
        teachers.setValue(filtered);
    }
}
