package com.example.myduet.repositories;

import android.content.Context;
import android.util.Log;
import com.example.myduet.R;
import com.example.myduet.models.EmergencyCategory;
import com.example.myduet.models.EmergencyContact;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EmergencyRepository {
    private Context context;

    public EmergencyRepository() {
    }

    public EmergencyRepository(Context context) {
        this.context = context.getApplicationContext();
    }
    
    private static class EmergencyContactsContainer {
        List<EmergencyCategory> categories;
        List<EmergencyContact> medical;
        List<EmergencyContact> security;
        List<EmergencyContact> proctor;
        List<EmergencyContact> transport;
        List<EmergencyContact> halls;
        List<EmergencyContact> ict;
        List<EmergencyContact> offices;
        List<EmergencyContact> faculties;
        List<EmergencyContact> institutes;
        List<EmergencyContact> research_centers;
    }

    private EmergencyContactsContainer loadLocalContainer() {
        if (context == null) return null;
        File file = new File(context.getFilesDir(), "emergency_contacts.json");
        if (!file.exists()) return null;
        try {
            FileInputStream fis = new FileInputStream(file);
            int size = fis.available();
            byte[] buffer = new byte[size];
            int read = fis.read(buffer);
            fis.close();
            if (read > 0) {
                String json = new String(buffer, StandardCharsets.UTF_8);
                return new Gson().fromJson(json, EmergencyContactsContainer.class);
            }
        } catch (Exception e) {
            Log.e("EmergencyRepository", "Error reading local emergency contacts", e);
        }
        return null;
    }

    private void resolveCategoryIcons(List<EmergencyCategory> list) {
        if (list == null || context == null) return;
        for (EmergencyCategory cat : list) {
            if (cat.getIconResName() != null && !cat.getIconResName().isEmpty()) {
                int resId = context.getResources().getIdentifier(cat.getIconResName(), "drawable", context.getPackageName());
                if (resId != 0) {
                    cat.setIconRes(resId);
                }
            }
        }
    }

    private void resolveContactIcons(List<EmergencyContact> list) {
        if (list == null || context == null) return;
        for (EmergencyContact contact : list) {
            if (contact.getIconResName() != null && !contact.getIconResName().isEmpty()) {
                int resId = context.getResources().getIdentifier(contact.getIconResName(), "drawable", context.getPackageName());
                if (resId != 0) {
                    contact.setIconResId(resId);
                }
            }
        }
    }

    public List<EmergencyCategory> getCategories() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.categories != null) {
            resolveCategoryIcons(container.categories);
            return container.categories;
        }
        List<EmergencyCategory> categories = new ArrayList<>();
        categories.add(new EmergencyCategory("1", "Medical Services", "Medical Center • Ambulance", R.drawable.ic_medical_center, "2 Services", 0xFFFFE8E8, 0xFFE53935));
        categories.add(new EmergencyCategory("2", "Security Office", "Campus Security", R.drawable.ic_security, "1 Office", 0xFFE8F1FF, 0xFF1565C0));
        categories.add(new EmergencyCategory("3", "Proctor Office", "Chief Proctor & Student Discipline", R.drawable.ic_proctor, "1 Office", 0xFFF3E8FF, 0xFF7B1FA2));
        categories.add(new EmergencyCategory("4", "Transport Office", "Transport Officer • Bus Support", R.drawable.ic_transport, "1 Office", 0xFFE8F8EE, 0xFF2E7D32));
        categories.add(new EmergencyCategory("5", "Hall Offices", "5 Student Halls", R.drawable.ic_halls, "5 Halls", 0xFFFFF3E0, 0xFFEF6C00));
        categories.add(new EmergencyCategory("6", "ICT Cell", "Help Desk • Network Support", R.drawable.ic_ict, "2 Services", 0xFFE0F7FA, 0xFF00838F));
        categories.add(new EmergencyCategory("7", "Faculty Deans", "Deans of 4 Faculties", R.drawable.ic_school, "4 Faculties", 0xFFE0F2F1, 0xFF00695C));
        return categories;
    }

    public List<EmergencyContact> getMedicalContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.medical != null) {
            resolveContactIcons(container.medical);
            return container.medical;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("M1", "Medical Center", "Dr. Md. Aminul Islam", "", "+880 1711 000001", "medical@duet.ac.bd", "Near Central Mosque", "24/7 Service"));
        list.add(new EmergencyContact("M2", "Ambulance Contact", "Control Room", "", "+880 1711 000002", "", "Medical Center Gate", "24/7 Emergency"));
        return list;
    }

    public List<EmergencyContact> getSecurityContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.security != null) {
            resolveContactIcons(container.security);
            return container.security;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("S1", "Chief Security Officer", "Mr. Security Chief", "", "+880 1711 000003", "security@duet.ac.bd", "Main Gate Office", "9 AM - 5 PM"));
        list.add(new EmergencyContact("S2", "Security Control Room", "Night Shift In-charge", "", "+880 1711 000004", "", "Admin Building", "24/7 Patrol"));
        return list;
    }

    public List<EmergencyContact> getProctorContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.proctor != null) {
            resolveContactIcons(container.proctor);
            return container.proctor;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("P1", "Chief Proctor", "Prof. Dr. X", "", "+880 1711 000005", "proctor@duet.ac.bd", "Admin Building", "9 AM - 5 PM"));
        list.add(new EmergencyContact("P2", "Assistant Proctor", "Dr. Y", "", "+880 1711 000006", "ap@duet.ac.bd", "Admin Building", "9 AM - 5 PM"));
        return list;
    }

    public List<EmergencyContact> getTransportContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.transport != null) {
            resolveContactIcons(container.transport);
            return container.transport;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("T1", "Transport Office", "Transport In-charge", "", "+880 1711 000007", "transport@duet.ac.bd", "Transport Garage", "8 AM - 8 PM"));
        list.add(new EmergencyContact("T2", "Transport Officer", "Mr. Driver Lead", "", "+880 1711 000008", "", "Transport Garage", "8 AM - 5 PM"));
        return list;
    }

    public List<EmergencyContact> getHallContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.halls != null) {
            resolveContactIcons(container.halls);
            return container.halls;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("H1", "Bijoy 24 Hall", "Prof. Dr. Md. Raju Ahmed", "Provost", "+880 1713 000001", "bijoy24@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        list.add(new EmergencyContact("H2", "Kazi Nazrul Islam Hall", "Prof. Dr. Md. Abdul Hannan", "Provost", "+880 1713 000002", "knih@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        list.add(new EmergencyContact("H3", "Madam Curie Hall", "Prof. Dr. Areful Azad Rizvi", "Provost", "+880 1713 000003", "mch@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        list.add(new EmergencyContact("H4", "Shaheed Tazuddin Ahmad Hall", "Prof. Dr. Md. Shaukat Osman", "Provost", "+880 1713 000004", "stah@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        list.add(new EmergencyContact("H5", "Shahid Muktijodda Hall", "Prof. Dr. Md. Zahir Uddin", "Provost", "+880 1713 000005", "smh@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        return list;
    }

    public List<EmergencyContact> getIctContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.ict != null) {
            resolveContactIcons(container.ict);
            return container.ict;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("I1", "ICT Help Desk", "Director, ICT", "", "+880 1711 000009", "ict@duet.ac.bd", "Old Academic Building", "9 AM - 5 PM"));
        list.add(new EmergencyContact("I2", "Network Support", "Network Engineer", "", "+880 1711 000010", "noc@duet.ac.bd", "Old Academic Building", "9 AM - 8 PM"));
        return list;
    }

    public List<EmergencyContact> getOfficeContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.offices != null) {
            resolveContactIcons(container.offices);
            return container.offices;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("O1", "Office of the Vice-Chancellor", "Prof. Dr. Engr. Mohammad Iqbal", "Muhammad Rakib-Ul-Hassan (PS to VC)", "09666-328001", "vc@duet.ac.bd", "Level-7, Shohid Abu Sayed Administrative Building, DUET, Gazipur", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_school));
        list.add(new EmergencyContact("O2", "Office of the Pro-Vice-Chancellor", "Prof. Dr. Ruma", "", "09666-328003", "pro_vc@duet.ac.bd", "Level-7, Shohid Abu Sayed Administrative Building, DUET, Gazipur", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_school));
        list.add(new EmergencyContact("O3", "Registrar Office", "Registrar Office", "", "09666-328005", "reg_duet@duet.ac.bd", "Level-6, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_office));
        list.add(new EmergencyContact("O4", "Office of the Controller of Examinations", "Controller of Examinations", "", "09666-328006", "coe@duet.ac.bd", "Level-4, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_book));
        list.add(new EmergencyContact("O5", "Comptroller Office", "Comptroller", "", "09666-328007", "comptroller@duet.ac.bd", "Level-5, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_office));
        list.add(new EmergencyContact("O6", "Engineering Office", "Chief Engineer", "", "09666-328008", "chiefeo@duet.ac.bd", "Level-3, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_settings));
        list.add(new EmergencyContact("O7", "Central Library", "Librarian", "", "09666-328010", "library@duet.ac.bd", "Library Building, DUET", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_library));
        list.add(new EmergencyContact("O8", "ICT Cell", "ICT System Administrator", "", "09666-328009", "ictcell@duet.ac.bd", "Level-8, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_ict));
        list.add(new EmergencyContact("O9", "Medical Center", "Chief Medical Officer", "", "09666-328011", "alikhan72@duet.ac.bd", "Library Building, DUET", "8:00 AM - 4:00 PM (Sun-Thu) & 24/7 Emergency", R.drawable.ic_medical_center));
        list.add(new EmergencyContact("O10", "Audit Cell", "Audit Officer", "", "09666-328005", "audit@duet.ac.bd", "Level-5, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_shield));
        list.add(new EmergencyContact("O11", "Computer Center", "System Analyst", "", "09666-328013", "computercenter@duet.ac.bd", "Level-8, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_ict));
        list.add(new EmergencyContact("O12", "Students’ Welfare Office", "Director, DSW", "", "09666-328014", "directorsw@duet.ac.bd", "Level-1, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_profile));
        list.add(new EmergencyContact("O13", "Planning & Development Office", "Director, P&D", "", "09666-328015", "directorpnd211@duet.ac.bd", "Level-2, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_route));
        list.add(new EmergencyContact("O14", "Research & Extension Office", "Director, R&E", "", "09666-328005", "research@duet.ac.bd", "Level-9, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_research));
        list.add(new EmergencyContact("O15", "Transport Office", "Director, Transport", "", "09666-328017", "director.transport@duet.ac.bd", "Level-1, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_transport));
        list.add(new EmergencyContact("O16", "Consultancy Research & Testing Service Office", "Director, CRTS", "", "09666-328018", "director.crts@duet.ac.bd", "Level-9, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_research));
        list.add(new EmergencyContact("O17", "Physical Education Center", "Director, PEC", "", "09666-328019", "director_pec@duet.ac.bd", "Level-1, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_monument));
        list.add(new EmergencyContact("O18", "Institutional Quality Assurance Cell", "Director, IQAC", "", "09666-328020", "iqac@duet.ac.bd", "Level-9, Shohid Abu Sayed Administrative Building", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_shield));
        return list;
    }

    public List<EmergencyContact> getFacultyContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.faculties != null) {
            resolveContactIcons(container.faculties);
            return container.faculties;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("F1", "Faculty of Civil Engineering", "Dr. Mohammad Nazim Uddin", "Dean", "09666-327600", "dean_ce@duet.ac.bd", "DUET Campus, Gazipur", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_school));
        list.add(new EmergencyContact("F2", "Faculty of Electrical and Electronic Engineering", "Dr. Ruma", "Dean", "09666-327700", "dean_eee@duet.ac.bd", "DUET Campus, Gazipur", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_school));
        list.add(new EmergencyContact("F3", "Faculty of Mechanical Engineering", "Dr. Md. Anowar Hossain", "Dean", "09666-327800", "dean_fme@duet.ac.bd", "DUET Campus, Gazipur", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_school));
        list.add(new EmergencyContact("F4", "Faculty of Science", "Dr. Md. Kamal-Al-Hassan", "Dean", "09666-327900", "dean_fs@duet.ac.bd", "DUET Campus, Gazipur", "8:00 AM - 4:00 PM (Sun-Thu)", R.drawable.ic_school));
        return list;
    }

    public List<EmergencyContact> getInstituteContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.institutes != null) {
            resolveContactIcons(container.institutes);
            return container.institutes;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("IN1", "Institute of Information & Communication Technology", "Director, IICT", "", "+880 1711 000201", "iict@duet.ac.bd", "Old Academic Building, Room 402", "9 AM - 5 PM"));
        list.add(new EmergencyContact("IN2", "Institute of Water and Environment", "Director, IWE", "", "+880 1711 000202", "iwe@duet.ac.bd", "Civil Engineering Building", "9 AM - 5 PM"));
        list.add(new EmergencyContact("IN3", "Institute of Energy Engineering", "Director, IEE", "", "+880 1711 000203", "iee@duet.ac.bd", "Mechanical Engineering Building", "9 AM - 5 PM"));
        return list;
    }

    public List<EmergencyContact> getResearchCenterContacts() {
        EmergencyContactsContainer container = loadLocalContainer();
        if (container != null && container.research_centers != null) {
            resolveContactIcons(container.research_centers);
            return container.research_centers;
        }
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("R1", "Center for Climate Change & Sustainability Research", "Director, CCCSR", "", "+880 1711 000301", "cccsr@duet.ac.bd", "Old Academic Building", "9 AM - 5 PM"));
        list.add(new EmergencyContact("R2", "Institutional Quality Assurance Cell", "Director, IQAC", "", "+880 1711 000302", "iqac@duet.ac.bd", "Shohid Abu Sayed Administrative Building", "9 AM - 5 PM"));
        list.add(new EmergencyContact("R3", "Consultancy Research & Testing Service", "Director, CRTS", "", "+880 1711 000303", "crts@duet.ac.bd", "Respective Department Office", "9 AM - 5 PM"));
        return list;
    }
}