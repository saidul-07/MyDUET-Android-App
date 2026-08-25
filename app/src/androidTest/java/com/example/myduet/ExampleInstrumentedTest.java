package com.example.myduet;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.myduet.models.EmergencyCategory;
import com.example.myduet.models.EmergencyContact;
import com.example.myduet.models.Teacher;
import com.example.myduet.models.RoutineClass;
import com.example.myduet.repositories.EmergencyRepository;
import com.example.myduet.repositories.TeacherRepository;
import com.example.myduet.repositories.RoutineRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myduet", appContext.getPackageName());
    }

    @Test
    public void testDynamicUpdatesParsing() throws IOException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // 1. Prepare mock files in internal storage filesDir
        File filesDir = context.getFilesDir();

        // Mock emergency_contacts.json
        String emergencyJson = "{\n" +
                "  \"categories\": [\n" +
                "    {\n" +
                "      \"id\": \"1\",\n" +
                "      \"name\": \"Dynamic Medical Services\",\n" +
                "      \"description\": \"Dynamic Sub\",\n" +
                "      \"iconResName\": \"ic_medical_center\",\n" +
                "      \"count\": \"1 Service\",\n" +
                "      \"bgColor\": 0,\n" +
                "      \"iconTint\": 0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"medical\": [\n" +
                "    {\n" +
                "      \"id\": \"M1\",\n" +
                "      \"name\": \"Dynamic Medical Center\",\n" +
                "      \"personName\": \"Dynamic Doctor\",\n" +
                "      \"assistantName\": \"\",\n" +
                "      \"phone\": \"01799999999\",\n" +
                "      \"email\": \"dynamic@duet.ac.bd\",\n" +
                "      \"location\": \"Dynamic Room\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        File emergencyFile = new File(filesDir, "emergency_contacts.json");
        writeStringToFile(emergencyFile, emergencyJson);

        // Mock teachers/cse_teachers.json
        File teachersDir = new File(filesDir, "teachers");
        teachersDir.mkdirs();
        String teachersJson = "[\n" +
                "  {\n" +
                "    \"name\": \"Dynamic Teacher Name\",\n" +
                "    \"designation\": \"Dynamic Professor\",\n" +
                "    \"phone\": \"01711122233\",\n" +
                "    \"email\": \"dynamic_teacher@duet.ac.bd\",\n" +
                "    \"location\": \"Level-3\",\n" +
                "    \"imgUrl\": \"\",\n" +
                "    \"profileUrl\": \"\"\n" +
                "  }\n" +
                "]";
        File teacherFile = new File(teachersDir, "cse_teachers.json");
        writeStringToFile(teacherFile, teachersJson);

        // Mock routines/cse/cse_third_year_sec_a.json
        File routinesCseDir = new File(filesDir, "routines/cse");
        routinesCseDir.mkdirs();
        String routineJson = "{\n" +
                "  \"days\": {\n" +
                "    \"Sunday\": [\n" +
                "      {\n" +
                "        \"courseCode\": \"CSE-301\",\n" +
                "        \"courseName\": \"Dynamic Course\",\n" +
                "        \"type\": \"Theory\",\n" +
                "        \"time\": \"8:30 AM\",\n" +
                "        \"room\": \"Room 401\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        File routineFile = new File(routinesCseDir, "cse_third_year_sec_a.json");
        writeStringToFile(routineFile, routineJson);

        // 2. Invoke Repositories and verify
        try {
            // Test Emergency
            EmergencyRepository emergencyRepo = new EmergencyRepository(context);
            List<EmergencyCategory> categories = emergencyRepo.getCategories();
            assertEquals(1, categories.size());
            assertEquals("Dynamic Medical Services", categories.get(0).getName());

            List<EmergencyContact> medicalContacts = emergencyRepo.getMedicalContacts();
            assertEquals(1, medicalContacts.size());
            assertEquals("Dynamic Medical Center", medicalContacts.get(0).getName());
            assertEquals("Dynamic Doctor", medicalContacts.get(0).getPersonName());

            // Test Teachers
            TeacherRepository teacherRepo = new TeacherRepository(context);
            List<Teacher> teachers = teacherRepo.loadTeachers("cse");
            assertEquals(1, teachers.size());
            assertEquals("Dynamic Teacher Name", teachers.get(0).getName());
            assertEquals("Dynamic Professor", teachers.get(0).getDesignation());

            // Test Routine
            RoutineRepository routineRepo = new RoutineRepository();
            List<RoutineClass> routine = routineRepo.getRoutine(context, "cse", "3rd", "A", "Sunday");
            assertEquals(1, routine.size());
            assertEquals("CSE-301", routine.get(0).getCourseCode());
            assertEquals("Dynamic Course", routine.get(0).getCourseName());
            assertEquals("Theory", routine.get(0).getType());
            assertEquals("8:30 AM", routine.get(0).getTime());
            assertEquals("Room 401", routine.get(0).getRoom());

        } finally {
            // 3. Cleanup files
            emergencyFile.delete();
            teacherFile.delete();
            routineFile.delete();
            deleteRecursive(teachersDir);
            deleteRecursive(new File(filesDir, "routines"));
        }
    }

    private void writeStringToFile(File file, String data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        fileOrDirectory.delete();
    }
}