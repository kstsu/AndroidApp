package com.example.studentinfoapp;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class StudentStoreTest {
    @Test public void sampleAccountRejectsWrongPassword() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        StudentStore store = new StudentStore(context);
        assertNull(store.login("2024-100001", "incorrect"));
        assertNull(store.login("missing-student", "Student@123"));
        StudentStore.Student sample = store.login("2024-100001", "Student@123");
        assertNotNull(sample);
        assertEquals("2024-100001", sample.number);
        store.logout();
        assertNull(store.currentStudent());
    }

    @Test public void registrationPersistsProfileAndSession() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String number = "test-" + System.currentTimeMillis();
        try {
            StudentStore store = new StudentStore(context);
            store.register(number, "Isabella Santos", "BSCS-ML", "3rd Year", "TestPass123");
            assertEquals(number, new StudentStore(context).currentStudent().number);
            store.logout();
            assertNull(store.currentStudent());
            assertNull(store.login(number, "wrong-password"));
            StudentStore.Student saved = store.login(number.toUpperCase(java.util.Locale.ROOT), "TestPass123");
            assertNotNull(saved);
            assertEquals("Isabella Santos", saved.name);
            assertEquals("BSCS-ML", saved.program);
            assertEquals("3rd Year", saved.year);
            try {
                store.register(number, "Duplicate", "BSCS", "1st Year", "TestPass123");
                fail("Duplicate student numbers must be rejected");
            } catch (IllegalArgumentException expected) { /* Expected. */ }
        } finally {
            context.getSharedPreferences("student_accounts", Context.MODE_PRIVATE).edit()
                    .remove("student_" + number).remove("active_student").commit();
        }
    }

    @Test public void profileEditsKeepStudentNumberAndLoginCredentials() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String number = "edit-" + System.currentTimeMillis();
        StudentStore store = new StudentStore(context);
        try {
            store.register(number, "Original Name", "BSCS", "1st Year", "TestPass123");
            StudentStore.Student updated = store.updateCurrentProfile("Edited Name", "BSIT", "2nd Year");
            assertNotNull(updated);
            assertEquals(number, updated.number);
            assertEquals("Edited Name", new StudentStore(context).currentStudent().name);
            store.logout();
            assertNull(store.updateCurrentProfile("Ignored", "Ignored", "3rd Year"));
            StudentStore.Student saved = store.login(number, "TestPass123");
            assertNotNull(saved);
            assertEquals("Edited Name", saved.name);
            assertEquals("BSIT", saved.program);
            assertEquals("2nd Year", saved.year);
        } finally {
            context.getSharedPreferences("student_accounts", Context.MODE_PRIVATE).edit()
                    .remove("student_" + number).remove("active_student").commit();
        }
    }
}
