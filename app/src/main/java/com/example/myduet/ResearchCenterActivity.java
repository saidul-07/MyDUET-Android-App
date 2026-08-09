package com.example.myduet;

import com.example.myduet.models.EmergencyContact;
import com.example.myduet.repositories.EmergencyRepository;
import java.util.List;

public class ResearchCenterActivity extends BaseContactActivity {
    @Override
    protected String getToolbarTitle() {
        return "Research Center Directory";
    }

    @Override
    protected List<EmergencyContact> getContacts() {
        return new EmergencyRepository().getResearchCenterContacts();
    }
}
