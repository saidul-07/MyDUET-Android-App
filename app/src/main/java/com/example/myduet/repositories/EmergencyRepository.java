package com.example.myduet.repositories;

import com.example.myduet.R;
import com.example.myduet.models.EmergencyCategory;
import com.example.myduet.models.EmergencyContact;
import java.util.ArrayList;
import java.util.List;

public class EmergencyRepository {

    public List<EmergencyCategory> getCategories() {
        List<EmergencyCategory> categories = new ArrayList<>();
        categories.add(new EmergencyCategory("1", "Medical Services", "Medical Center • Ambulance", R.drawable.ic_medical_center, "2 Services", 0xFFFFE8E8, 0xFFE53935));
        categories.add(new EmergencyCategory("2", "Security Office", "Campus Security", R.drawable.ic_security, "1 Office", 0xFFE8F1FF, 0xFF1565C0));
        categories.add(new EmergencyCategory("3", "Proctor Office", "Chief Proctor & Student Discipline", R.drawable.ic_proctor, "1 Office", 0xFFF3E8FF, 0xFF7B1FA2));
        categories.add(new EmergencyCategory("4", "Transport Office", "Transport Officer • Bus Support", R.drawable.ic_transport, "1 Office", 0xFFE8F8EE, 0xFF2E7D32));
        categories.add(new EmergencyCategory("5", "Hall Offices", "5 Student Halls", R.drawable.ic_halls, "5 Halls", 0xFFFFF3E0, 0xFFEF6C00));
        categories.add(new EmergencyCategory("6", "ICT Cell", "Help Desk • Network Support", R.drawable.ic_ict, "2 Services", 0xFFE0F7FA, 0xFF00838F));
        return categories;
    }

    public List<EmergencyContact> getMedicalContacts() {
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("M1", "Medical Center", "Dr. Md. Aminul Islam", "", "+880 1711 000001", "medical@duet.ac.bd", "Near Central Mosque", "24/7 Service"));
        list.add(new EmergencyContact("M2", "Ambulance Contact", "Control Room", "", "+880 1711 000002", "", "Medical Center Gate", "24/7 Emergency"));
        return list;
    }

    public List<EmergencyContact> getSecurityContacts() {
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("S1", "Chief Security Officer", "Mr. Security Chief", "", "+880 1711 000003", "security@duet.ac.bd", "Main Gate Office", "9 AM - 5 PM"));
        list.add(new EmergencyContact("S2", "Security Control Room", "Night Shift In-charge", "", "+880 1711 000004", "", "Admin Building", "24/7 Patrol"));
        return list;
    }

    public List<EmergencyContact> getProctorContacts() {
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("P1", "Chief Proctor", "Prof. Dr. X", "", "+880 1711 000005", "proctor@duet.ac.bd", "Admin Building", "9 AM - 5 PM"));
        list.add(new EmergencyContact("P2", "Assistant Proctor", "Dr. Y", "", "+880 1711 000006", "ap@duet.ac.bd", "Admin Building", "9 AM - 5 PM"));
        return list;
    }

    public List<EmergencyContact> getTransportContacts() {
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("T1", "Transport Office", "Transport In-charge", "", "+880 1711 000007", "transport@duet.ac.bd", "Transport Garage", "8 AM - 8 PM"));
        list.add(new EmergencyContact("T2", "Transport Officer", "Mr. Driver Lead", "", "+880 1711 000008", "", "Transport Garage", "8 AM - 5 PM"));
        return list;
    }

    public List<EmergencyContact> getHallContacts() {
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("H1", "Bijoy 24 Hall", "Prof. Dr. Md. Raju Ahmed", "Provost", "+880 1713 000001", "bijoy24@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        list.add(new EmergencyContact("H2", "Kazi Nazrul Islam Hall", "Prof. Dr. Md. Abdul Hannan", "Provost", "+880 1713 000002", "knih@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        list.add(new EmergencyContact("H3", "Madam Curie Hall", "Prof. Dr. Areful Azad Rizvi", "Provost", "+880 1713 000003", "mch@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        list.add(new EmergencyContact("H4", "Shaheed Tazuddin Ahmad Hall", "Prof. Dr. Md. Shaukat Osman", "Provost", "+880 1713 000004", "stah@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        list.add(new EmergencyContact("H5", "Shahid Muktijodda Hall", "Prof. Dr. Md. Zahir Uddin", "Provost", "+880 1713 000005", "smh@duet.ac.bd", "DUET Campus, Gazipur", "9:00 AM - 5:00 PM"));
        return list;
    }

    public List<EmergencyContact> getIctContacts() {
        List<EmergencyContact> list = new ArrayList<>();
        list.add(new EmergencyContact("I1", "ICT Help Desk", "Director, ICT", "", "+880 1711 000009", "ict@duet.ac.bd", "Old Academic Building", "9 AM - 5 PM"));
        list.add(new EmergencyContact("I2", "Network Support", "Network Engineer", "", "+880 1711 000010", "noc@duet.ac.bd", "Old Academic Building", "9 AM - 8 PM"));
        return list;
    }
}