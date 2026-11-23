package com.example.studentinfoapp;

import android.content.Intent;
import android.app.Activity;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
