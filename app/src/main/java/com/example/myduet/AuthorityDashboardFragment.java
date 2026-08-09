package com.example.myduet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.myduet.adapters.EventAdapter;
import com.example.myduet.databinding.FragmentAuthorityDashboardBinding;
import com.example.myduet.models.Event;
import com.example.myduet.models.User;
import com.example.myduet.viewmodels.EventViewModel;

import java.util.ArrayList;
import java.util.List;

public class AuthorityDashboardFragment extends Fragment implements EventAdapter.OnEventClickListener {

    private FragmentAuthorityDashboardBinding binding;
    private EventViewModel viewModel;
    private EventAdapter adapter;
    private final List<Event> eventList = new ArrayList<>();
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAuthorityDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        currentUser = viewModel.getLoggedInUser().getValue();

        if (currentUser == null) {
            Toast.makeText(getContext(), "Access Denied: Please Login", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).popBackStack(R.id.eventListFragment, false);
            return;
        }

        // Setup UI Header
        binding.tvAuthName.setText(currentUser.getName());
        binding.tvAuthRole.setText("Role: " + currentUser.getRole());

        // Setup RecyclerView
        binding.rvAuthEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventAdapter(eventList, this);
        binding.rvAuthEvents.setAdapter(adapter);

        // Fetch user-specific events
        observeEvents(view);

        // Logout
        binding.btnAuthLogout.setOnClickListener(v -> {
            viewModel.logout();
            Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
        });

        // Create Event FAB
        binding.fabCreateEvent.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(
                    R.id.action_authorityDashboardFragment_to_createEditEventFragment
            );
        });
    }

    private void observeEvents(View view) {
        LiveData<List<Event>> source;
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            binding.tvMyEventsHeader.setText("All University Events (Admin Mode)");
            source = viewModel.getEvents(); // ADMIN can see and edit all events
        } else {
            binding.tvMyEventsHeader.setText("My Published Events");
            source = viewModel.getMyEvents(currentUser.getUserId());
        }

        source.observe(getViewLifecycleOwner(), events -> {
            eventList.clear();
            if (events != null && !events.isEmpty()) {
                eventList.addAll(events);
                binding.layoutAuthEmpty.setVisibility(View.GONE);
                binding.rvAuthEvents.setVisibility(View.VISIBLE);
            } else {
                binding.layoutAuthEmpty.setVisibility(View.VISIBLE);
                binding.rvAuthEvents.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onEventClick(Event event) {
        // Show management options: Edit, Cancel, Delete, View Details
        String[] options = {"View Details", "Edit Event", "Cancel Event", "Delete Event"};
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Manage Event")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // View details
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("event", event);
                            if (getView() != null) {
                                Navigation.findNavController(getView()).navigate(
                                        R.id.action_authorityDashboardFragment_to_eventDetailFragment, bundle
                                );
                            }
                            break;
                        case 1:
                            // Edit
                            Bundle editBundle = new Bundle();
                            editBundle.putSerializable("edit_event", event);
                            if (getView() != null) {
                                Navigation.findNavController(getView()).navigate(
                                        R.id.action_authorityDashboardFragment_to_createEditEventFragment, editBundle
                                );
                            }
                            break;
                        case 2:
                            // Cancel
                            confirmCancelEvent(event);
                            break;
                        case 3:
                            // Delete
                            confirmDeleteEvent(event);
                            break;
                    }
                })
                .show();
    }

    @Override
    public void onRegisterClick(Event event) {
        // Open browser
        try {
            android.net.Uri uri = android.net.Uri.parse(event.getRegistrationUrl());
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid link", Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmCancelEvent(Event event) {
        if ("Cancelled".equalsIgnoreCase(event.getStatus())) {
            Toast.makeText(getContext(), "Event is already cancelled", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Event")
                .setMessage("Are you sure you want to cancel this event? This will mark the event as cancelled in the student app without deleting its history.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    viewModel.cancelEvent(event.getEventId(), () -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Event Cancelled Successfully", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void confirmDeleteEvent(Event event) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to permanently delete this event from the database?")
                .setPositiveButton("Delete Permanently", (dialog, which) -> {
                    viewModel.deleteEvent(event.getEventId(), () -> {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Event Deleted Successfully", Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
