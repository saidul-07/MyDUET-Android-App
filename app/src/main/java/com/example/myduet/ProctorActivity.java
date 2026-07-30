package com.example.myduet;

import com.example.myduet.models.EmergencyContact;
import com.example.myduet.repositories.EmergencyRepository;
import java.util.List;

public class ProctorActivity extends BaseContactActivity {
    @Override
    protected String getToolbarTitle() {
        return "Proctor Office";
    }

    @Override
    protected List<EmergencyContact> getContacts() {
        return new EmergencyRepository().getProctorContacts();
    }
}