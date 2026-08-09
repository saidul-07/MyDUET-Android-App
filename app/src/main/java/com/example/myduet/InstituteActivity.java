package com.example.myduet;

import com.example.myduet.models.EmergencyContact;
import com.example.myduet.repositories.EmergencyRepository;
import java.util.List;

public class InstituteActivity extends BaseContactActivity {
    @Override
    protected String getToolbarTitle() {
        return "Institute Directory";
    }

    @Override
    protected List<EmergencyContact> getContacts() {
        return new EmergencyRepository().getInstituteContacts();
    }
}
