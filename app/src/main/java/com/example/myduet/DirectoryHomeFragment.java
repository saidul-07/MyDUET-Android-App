package com.example.myduet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DirectoryHomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_directory_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnOffice).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OfficeActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnFaculty).setOnClickListener(v -> {
            if (getActivity() instanceof TeachersActivity) {
                ((TeachersActivity) getActivity()).navigateToDeptList();
            }
        });

        view.findViewById(R.id.btnInstitute).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InstituteActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnHall).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HallListActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.btnResearch).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResearchCenterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof TeachersActivity) {
            ((TeachersActivity) getActivity()).updateToolbar("DUET Directory");
        }
    }
}
