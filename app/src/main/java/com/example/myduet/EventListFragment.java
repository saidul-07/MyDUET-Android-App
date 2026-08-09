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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myduet.adapters.EventAdapter;
import com.example.myduet.databinding.FragmentEventListBinding;
import com.example.myduet.models.Event;
import com.example.myduet.viewmodels.EventViewModel;

import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FragmentEventListBinding binding;
    private EventViewModel viewModel;
    private EventAdapter adapter;
    private final List<Event> eventList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEventListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        // RecyclerView Setup
        binding.rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(eventList, this);
        binding.rvEvents.setAdapter(adapter);

        // Fetch events
        viewModel.loadEvents();

        // Observers
        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            binding.swipeRefresh.setRefreshing(false);
            eventList.clear();
            if (events != null && !events.isEmpty()) {
                eventList.addAll(events);
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.rvEvents.setVisibility(View.VISIBLE);
            } else {
                binding.layoutEmpty.setVisibility(View.VISIBLE);
                binding.rvEvents.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        });

        // Swipe Refresh
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.loadEvents());

        // Search text watcher
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Chip group filter logic
        binding.chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipAll) {
                viewModel.setCategory("All");
            } else if (checkedId == R.id.chipUpcoming) {
                viewModel.setCategory("Upcoming");
            } else if (checkedId == R.id.chipOngoing) {
                viewModel.setCategory("Ongoing");
            } else if (checkedId == R.id.chipUniversity) {
                viewModel.setCategory("University");
            } else if (checkedId == R.id.chipClubs) {
                viewModel.setCategory("Clubs");
            }
        });

        // Fab Portal click navigation
        binding.fabPortal.setOnClickListener(v -> {
            if (viewModel.getLoggedInUser().getValue() != null) {
                Navigation.findNavController(view).navigate(R.id.action_eventListFragment_to_authorityDashboardFragment);
            } else {
                Navigation.findNavController(view).navigate(R.id.action_eventListFragment_to_authorityLoginFragment);
            }
        });

        // Update FAB icon if user is logged in
        viewModel.getLoggedInUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                binding.fabPortal.setText("Dashboard");
            } else {
                binding.fabPortal.setText("Authority Portal");
            }
        });
    }

    @Override
    public void onEventClick(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("event", event);
        if (getView() != null) {
            Navigation.findNavController(getView()).navigate(
                    R.id.action_eventListFragment_to_eventDetailFragment, bundle
            );
        }
    }

    @Override
    public void onRegisterClick(Event event) {
        if (event.getRegistrationUrl() != null && !event.getRegistrationUrl().trim().isEmpty()) {
            openUrlInBrowser(event.getRegistrationUrl());
        }
    }

    private void openUrlInBrowser(String url) {
        try {
            android.net.Uri uri = android.net.Uri.parse(url);
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            android.widget.Toast.makeText(getContext(), "Invalid registration link", android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
