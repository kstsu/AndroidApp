package com.example.studentinfoapp;

import android.content.Intent;
import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class PortalUiTest {
    @Test public void loginAndRegistrationRenderAndNavigate() {
        android.app.Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        android.content.Context context = instrumentation.getTargetContext();
        new StudentStore(context).logout();
        Activity activity = instrumentation.startActivitySync(new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        try {
            instrumentation.waitForIdleSync();
            instrumentation.runOnMainSync(() -> {
                View root = activity.getWindow().getDecorView();
                assertNotNull(find(root, "Welcome back"));
                assertTrue(find(root, "New student? Create an account").performClick());
                assertNotNull(find(root, "Create an account"));
                assertNotNull(find(root, "Year level"));
                assertTrue(find(root, "Already registered? Sign in").performClick());
                assertNotNull(find(root, "Welcome back"));
            });
        } finally { instrumentation.runOnMainSync(activity::finish); }
    }

    @Test public void profileEditsCanBeCancelledOrSavedAndDayPickerUpdatesSchedule() {
        android.app.Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        android.content.Context context = instrumentation.getTargetContext();
        String number = "ui-edit-" + System.currentTimeMillis();
        new StudentStore(context).register(number, "Original Name", "BSCS", "1st Year", "TestPass123");
        Activity activity = instrumentation.startActivitySync(new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        try {
            instrumentation.runOnMainSync(() -> {
                assertTrue(activity.findViewById(R.id.profileEdit).performClick());
                TextInputEditText name = activity.findViewById(R.id.editProfileName);
                assertEquals("Original Name", name.getText().toString());
                TextView readOnlyNumber = activity.findViewById(R.id.editProfileNumber);
                assertEquals(number, readOnlyNumber.getText().toString());
                name.setText("Unsaved Name");
                assertTrue(activity.findViewById(R.id.editProfileCancel).performClick());
                assertEquals("Original Name", ((TextView) activity.findViewById(R.id.profileName)).getText().toString());
                assertTrue(activity.findViewById(R.id.profileEdit).performClick());
                ((TextInputEditText) activity.findViewById(R.id.editProfileName)).setText("Updated Name");
                assertTrue(activity.findViewById(R.id.editProfileSave).performClick());
                assertEquals("Updated Name", ((TextView) activity.findViewById(R.id.profileName)).getText().toString());
                ((Spinner) activity.findViewById(R.id.profileScheduleDay)).setSelection(2);
            });
            instrumentation.waitForIdleSync();
            instrumentation.runOnMainSync(() -> {
                TextView status = activity.findViewById(R.id.profileScheduleStatus);
                assertEquals(activity.getString(R.string.profile_schedule_free), status.getText().toString());
                ((Spinner) activity.findViewById(R.id.profileScheduleDay)).setSelection(0);
            });
            instrumentation.waitForIdleSync();
            instrumentation.runOnMainSync(() -> {
                ViewGroup lessons = activity.findViewById(R.id.profileScheduleLessons);
                assertEquals(4, lessons.getChildCount());
            });
        } finally {
            instrumentation.runOnMainSync(activity::finish);
            context.getSharedPreferences("student_accounts", android.content.Context.MODE_PRIVATE).edit()
                    .remove("student_" + number).remove("active_student").commit();
        }
    }

    private View find(View view, String text) {
        if (view instanceof TextView && text.contentEquals(((TextView) view).getText())) return view;
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View match = find(group.getChildAt(i), text);
                if (match != null) return match;
            }
        }
        return null;
    }
}
