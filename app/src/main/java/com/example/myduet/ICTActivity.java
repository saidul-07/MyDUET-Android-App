package com.example.myduet;

import com.example.myduet.models.EmergencyContact;
import com.example.myduet.repositories.EmergencyRepository;
import java.util.List;

public class ICTActivity extends BaseContactActivity {
    @Override
    protected String getToolbarTitle() {
        return "ICT Cell";
    }

    @Override
    protected List<EmergencyContact> getContacts() {
        return new EmergencyRepository().getIctContacts();
    }
}