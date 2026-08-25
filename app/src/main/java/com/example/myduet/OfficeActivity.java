package com.example.myduet;

import com.example.myduet.models.EmergencyContact;
import com.example.myduet.repositories.EmergencyRepository;
import java.util.List;

public class OfficeActivity extends BaseContactActivity {
    @Override
    protected String getToolbarTitle() {
        return "Offices";
    }

    @Override
    protected List<EmergencyContact> getContacts() {
        return new EmergencyRepository().getOfficeContacts();
    }
}
