package com.example.myduet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.myduet.databinding.FragmentCreateEditEventBinding;
import com.example.myduet.models.Event;
import com.example.myduet.models.User;
import com.example.myduet.viewmodels.EventViewModel;

import java.util.Calendar;
import java.util.Locale;

public class CreateEditEventFragment extends Fragment {

    private FragmentCreateEditEventBinding binding;
    private EventViewModel viewModel;
    private Event editEvent = null;
    private boolean isEditMode = false;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateEditEventBinding.inflate(inflater, container, false);
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

        // Check if editing
        if (getArguments() != null && getArguments().containsKey("edit_event")) {
            editEvent = (Event) getArguments().getSerializable("edit_event");
            isEditMode = true;
        }

        setupFormUI();
    }

    private void setupFormUI() {
        // Toggle Registration Details based on required switch
        binding.swRegRequired.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.layoutRegistrationDetails.setVisibility(View.VISIBLE);
            } else {
                binding.layoutRegistrationDetails.setVisibility(View.GONE);
                binding.etRegDeadline.setText("");
                binding.etRegUrl.setText("");
            }
        });

        // Setup Date and Time Pickers
        binding.etEventDate.setOnClickListener(v -> showDatePicker(binding.etEventDate));
        binding.etStartTime.setOnClickListener(v -> showTimePicker(binding.etStartTime));
        binding.etEndTime.setOnClickListener(v -> showTimePicker(binding.etEndTime));
        binding.etRegDeadline.setOnClickListener(v -> showDateTimePicker(binding.etRegDeadline));

        // Populate fields if in edit mode
        if (isEditMode && editEvent != null) {
            binding.tvFormTitle.setText("Edit Event Details");
            binding.btnPublish.setText("Save Changes");

            binding.etEventName.setText(editEvent.getTitle());
            if ("Club".equalsIgnoreCase(editEvent.getType())) {
                binding.rbClub.setChecked(true);
            } else {
                binding.rbUniversity.setChecked(true);
            }

            binding.etOrganizerName.setText(editEvent.getOrganizerName());
            binding.etDescription.setText(editEvent.getDescription());
            binding.etBannerUrl.setText(editEvent.getBannerUrl());
            binding.etEventDate.setText(editEvent.getEventDate());
            binding.etStartTime.setText(editEvent.getStartTime());
            binding.etEndTime.setText(editEvent.getEndTime());
            binding.etVenue.setText(editEvent.getVenue());

            binding.swRegRequired.setChecked(editEvent.isRegistrationRequired());
            if (editEvent.isRegistrationRequired()) {
                binding.layoutRegistrationDetails.setVisibility(View.VISIBLE);
                binding.etRegDeadline.setText(editEvent.getRegistrationDeadline());
                binding.etRegUrl.setText(editEvent.getRegistrationUrl());
            }

            binding.etContactName.setText(editEvent.getContactName());
            binding.etContactEmail.setText(editEvent.getContactEmail());
            binding.etContactPhone.setText(editEvent.getContactPhone());

            // Optional Info
            if (editEvent.getMaxParticipants() != null && editEvent.getMaxParticipants() > 0) {
                binding.etMaxParticipants.setText(String.valueOf(editEvent.getMaxParticipants()));
            }
            binding.etSocialMediaUrl.setText(editEvent.getSocialMediaUrl());
            binding.etAdditionalInfo.setText(editEvent.getAdditionalInfo());
        }

        // Handle publish button click
        binding.btnPublish.setOnClickListener(v -> handlePublish());
    }

    private void handlePublish() {
        // Reset errors
        resetErrors();

        // Retrieve values
        String name = binding.etEventName.getText().toString().trim();
        String organizer = binding.etOrganizerName.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String bannerUrl = binding.etBannerUrl.getText().toString().trim();
        String date = binding.etEventDate.getText().toString().trim();
        String startTime = binding.etStartTime.getText().toString().trim();
        String endTime = binding.etEndTime.getText().toString().trim();
        String venue = binding.etVenue.getText().toString().trim();
        boolean regRequired = binding.swRegRequired.isChecked();
        String regDeadline = binding.etRegDeadline.getText().toString().trim();
        String regUrl = binding.etRegUrl.getText().toString().trim();
        String contactName = binding.etContactName.getText().toString().trim();
        String contactEmail = binding.etContactEmail.getText().toString().trim();
        String contactPhone = binding.etContactPhone.getText().toString().trim();

        // Optional values
        String maxPartStr = binding.etMaxParticipants.getText().toString().trim();
        String socialMedia = binding.etSocialMediaUrl.getText().toString().trim();
        String addInfo = binding.etAdditionalInfo.getText().toString().trim();

        // Validation
        boolean isValid = true;

        if (name.isEmpty()) {
            binding.tilEventName.setError("Event Name is required");
            isValid = false;
        }
        if (organizer.isEmpty()) {
            binding.tilOrganizerName.setError("Organizer Name is required");
            isValid = false;
        }
        if (description.isEmpty()) {
            binding.tilDescription.setError("Description is required");
            isValid = false;
        }
        if (bannerUrl.isEmpty()) {
            binding.tilBannerUrl.setError("Banner image URL is required");
            isValid = false;
        }
        if (date.isEmpty()) {
            binding.tilEventDate.setError("Required");
            isValid = false;
        }
        if (startTime.isEmpty()) {
            binding.tilStartTime.setError("Required");
            isValid = false;
        }
        if (endTime.isEmpty()) {
            binding.tilEndTime.setError("Required");
            isValid = false;
        }
        if (venue.isEmpty()) {
            binding.tilVenue.setError("Venue is required");
            isValid = false;
        }

        if (regRequired) {
            if (regDeadline.isEmpty()) {
                binding.tilRegDeadline.setError("Deadline is required");
                isValid = false;
            }
            if (regUrl.isEmpty()) {
                binding.tilRegUrl.setError("Registration link is required");
                isValid = false;
            }
        }

        if (contactName.isEmpty()) {
            binding.tilContactName.setError("Contact Name is required");
            isValid = false;
        }
        if (contactEmail.isEmpty()) {
            binding.tilContactEmail.setError("Contact Email is required");
            isValid = false;
        }
        if (contactPhone.isEmpty()) {
            binding.tilContactPhone.setError("Contact Phone is required");
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(getContext(), "Please correct the highlighted errors.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Map to Event Object
        Event event = isEditMode ? editEvent : new Event();
        event.setTitle(name);
        event.setType(binding.rbClub.isChecked() ? "Club" : "University");
        event.setClubName(binding.rbClub.isChecked() ? organizer : "");
        event.setOrganizerName(organizer);
        event.setDescription(description);
        event.setBannerUrl(bannerUrl);
        event.setEventDate(date);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setVenue(venue);
        event.setRegistrationRequired(regRequired);
        event.setRegistrationDeadline(regDeadline);
        event.setRegistrationUrl(regUrl);
        event.setContactName(contactName);
        event.setContactEmail(contactEmail);
        event.setContactPhone(contactPhone);

        // Map optional parameters
        if (!maxPartStr.isEmpty()) {
            event.setMaxParticipants(Integer.parseInt(maxPartStr));
        } else {
            event.setMaxParticipants(null);
        }
        event.setSocialMediaUrl(socialMedia);
        event.setAdditionalInfo(addInfo);

        // Show progress dialog to handle transition time professionally
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage(isEditMode ? "Saving changes..." : "Publishing event...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        binding.btnPublish.setEnabled(false);

        if (!isEditMode) {
            event.setStatus("Active");
            event.setCreatedBy(currentUser.getUserId());
            viewModel.createEvent(event, () -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getContext(), "Event Published Successfully!", Toast.LENGTH_LONG).show();
                        androidx.navigation.fragment.NavHostFragment.findNavController(CreateEditEventFragment.this).navigateUp();
                    });
                }
            });
        } else {
            viewModel.updateEvent(event, () -> {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getContext(), "Event Details Saved!", Toast.LENGTH_LONG).show();
                        androidx.navigation.fragment.NavHostFragment.findNavController(CreateEditEventFragment.this).navigateUp();
                    });
                }
            });
        }
    }

    private void showDatePicker(EditText editText) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String dateStr = String.format(Locale.ENGLISH, "%d-%02d-%02d", year, month + 1, dayOfMonth);
            editText.setText(dateStr);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }

    private void showTimePicker(EditText editText) {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog picker = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
            String timeStr = String.format(Locale.ENGLISH, "%02d:%02d", hourOfDay, minute);
            editText.setText(timeStr);
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        picker.show();
    }

    private void showDateTimePicker(EditText editText) {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            TimePickerDialog timePicker = new TimePickerDialog(requireContext(), (view2, hourOfDay, minute) -> {
                String dateTimeStr = String.format(Locale.ENGLISH, "%d-%02d-%02d %02d:%02d", year, month + 1, dayOfMonth, hourOfDay, minute);
                editText.setText(dateTimeStr);
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
            timePicker.show();
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void resetErrors() {
        binding.tilEventName.setError(null);
        binding.tilOrganizerName.setError(null);
        binding.tilDescription.setError(null);
        binding.tilBannerUrl.setError(null);
        binding.tilEventDate.setError(null);
        binding.tilStartTime.setError(null);
        binding.tilEndTime.setError(null);
        binding.tilVenue.setError(null);
        binding.tilRegDeadline.setError(null);
        binding.tilRegUrl.setError(null);
        binding.tilContactName.setError(null);
        binding.tilContactEmail.setError(null);
        binding.tilContactPhone.setError(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
