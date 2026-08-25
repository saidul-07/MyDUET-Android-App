package com.example.myduet;

import com.example.myduet.models.RoutineClass;
import com.example.myduet.models.RoutineData;
import com.google.gson.Gson;
import org.junit.Test;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testCseRoutinesParsing() throws IOException {
        File routinesDir = new File("src/main/assets/routines/cse");
        if (!routinesDir.exists()) {
            // In some environments, the working directory when running the test might be the project root
            routinesDir = new File("app/src/main/assets/routines/cse");
        }
        assertTrue("Routines directory must exist", routinesDir.exists());

        File[] jsonFiles = routinesDir.listFiles((dir, name) -> name.endsWith(".json"));
        assertNotNull("JSON files list must not be null", jsonFiles);
        assertTrue("There must be CSE routine JSON files", jsonFiles.length > 0);

        Gson gson = new Gson();
        for (File file : jsonFiles) {
            try (FileReader reader = new FileReader(file)) {
                RoutineData data = gson.fromJson(reader, RoutineData.class);
                assertNotNull("RoutineData must not be null for " + file.getName(), data);
                assertEquals("CSE", data.getDepartment());
                assertNotNull("Year must not be null for " + file.getName(), data.getYear());
                assertNotNull("Section must not be null for " + file.getName(), data.getSection());
                assertNotNull("Days map must not be null for " + file.getName(), data.getDays());

                for (Map.Entry<String, List<RoutineClass>> entry : data.getDays().entrySet()) {
                    assertNotNull("Day name must not be null", entry.getKey());
                    List<RoutineClass> classes = entry.getValue();
                    assertNotNull("Classes list must not be null for day: " + entry.getKey(), classes);
                    for (RoutineClass cls : classes) {
                        assertNotNull("Course code must not be null in " + file.getName(), cls.getCourseCode());
                        assertNotNull("Course name must not be null in " + file.getName(), cls.getCourseName());
                        assertFalse("Course name must not be empty or Unknown in " + file.getName() + " for " + cls.getCourseCode(), 
                                cls.getCourseName().trim().isEmpty() || "Unknown Course".equalsIgnoreCase(cls.getCourseName()));
                        assertNotNull("Type must not be null in " + file.getName(), cls.getType());
                        assertTrue("Type must be Theory or Lab in " + file.getName(),
                                "Theory".equals(cls.getType()) || "Lab".equals(cls.getType()));
                        assertNotNull("Time must not be null in " + file.getName(), cls.getTime());
                        assertNotNull("Room must not be null in " + file.getName(), cls.getRoom());
                    }
                }
            }
        }
    }
}