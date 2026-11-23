package com.example.studentinfoapp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/** A self-contained, offline student portal demo. */
public class MainActivity extends AppCompatActivity {
    private static final int BLUE = 0xFF18354B;
    private static final int INK = 0xFF152C3D;
    private static final int MUTED = 0xFF526673;
    private static final int CANVAS = 0xFFFCFDFD;
    private static final int SURFACE = 0xFFF0F4F6;
    private static final int BORDER = 0xFFD9E2E8;
    private static final int ERROR = 0xFFA32632;
    private StudentStore store;
    private LinearLayout content;
    private TextView message;
    private boolean registering;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        WindowCompat.enableEdgeToEdge(getWindow());
        store = new StudentStore(this);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (registering) showLogin();
                else { setEnabled(false); getOnBackPressedDispatcher().onBackPressed(); }
            }
        });
        StudentStore.Student student = store.currentStudent();
        if (student != null) showProfile(student);
        else if (state != null && state.getBoolean("registering")) showRegister();
        else showLogin();
    }

    @Override protected void onSaveInstanceState(Bundle out) {
        out.putBoolean("registering", registering);
        super.onSaveInstanceState(out);
    }

    private void page(int illustrationHeight) {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CANVAS);
        root.setFocusableInTouchMode(true);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(24), dp(20), dp(24), dp(28));
        // Constrain the form on tablets without fixing its height or clipping larger text.
        android.widget.FrameLayout frame = new android.widget.FrameLayout(this);
        final LinearLayout pageColumn = content;
        frame.addView(content, new android.widget.FrameLayout.LayoutParams(-1, -2, Gravity.TOP | Gravity.CENTER_HORIZONTAL));
        frame.addOnLayoutChangeListener((view, l, t, r, b, ol, ot, or, ob) -> {
            int width = Math.min(r - l, dp(520));
            if (pageColumn.getLayoutParams().width != width) {
                android.view.ViewGroup.LayoutParams params = pageColumn.getLayoutParams();
                params.width = width;
                pageColumn.setLayoutParams(params);
            }
        });
        scroll.addView(frame, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, -1));
        setContentView(root);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightNavigationBars(true);

        TextView wordmark = text("STUDENT PORTAL", 14, BLUE, true);
        wordmark.setGravity(Gravity.CENTER);
        wordmark.setLetterSpacing(0.08f);
        content.addView(wordmark, new LinearLayout.LayoutParams(-1, -2));
        space(20);
        if (illustrationHeight > 0) {
            ImageView art = new ImageView(this);
            art.setImageResource(R.drawable.portal_illustration);
            art.setScaleType(ImageView.ScaleType.FIT_CENTER);
            art.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            content.addView(art, new LinearLayout.LayoutParams(-1, dp(illustrationHeight)));
            space(20);
        }
        root.requestFocus();
    }

    private void heading(String title, String subtitle) {
        TextView display = text(title, 34, INK, true);
        display.setLetterSpacing(-0.02f);
        ViewCompat.setAccessibilityHeading(display, true);
        content.addView(display, new LinearLayout.LayoutParams(-1, -2));
        space(8);
        TextView summary = text(subtitle, 16, MUTED, false);
        content.addView(summary, new LinearLayout.LayoutParams(-1, -2));
        space(24);
    }

    private void openLayout(int layout) {
        setContentView(layout);
        View root = ((android.view.ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets edges = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            view.setPadding(edges.left, edges.top, edges.right, edges.bottom);
            return insets;
        });
        ViewCompat.requestApplyInsets(root);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightStatusBars(true);
        WindowCompat.getInsetsController(getWindow(), root).setAppearanceLightNavigationBars(true);
    }

    private void showLogin() {
        registering = false;
        openLayout(R.layout.activity_login);
        TextInputEditText number = findViewById(R.id.loginStudentNumber);
        TextInputEditText password = findViewById(R.id.loginPassword);
        TextInputLayout error = findViewById(R.id.loginPasswordLayout);
        View submit = findViewById(R.id.loginButton);
        submit.setOnClickListener(view -> {
            hideKeyboard();
            StudentStore.Student student = store.login(value(number), password.getText().toString());
            if (student == null) error.setError("Student number or password is incorrect.");
            else showProfile(student);
        });
        password.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);
        password.setOnEditorActionListener((view, action, event) ->
                action == android.view.inputmethod.EditorInfo.IME_ACTION_DONE && submit.performClick());
        findViewById(R.id.registerButton).setOnClickListener(view -> showRegister());
        findViewById(R.id.sampleButton).setOnClickListener(view -> {
            number.setText("2024-100001");
            password.setText("Student@123");
            error.setError(null);
        });
    }

    private void showRegister() {
        registering = true;
        openLayout(R.layout.activity_student_info);
        TextInputEditText name = findViewById(R.id.studentName);
        TextInputEditText number = findViewById(R.id.studentNumber);
        TextInputEditText program = findViewById(R.id.program);
        TextInputEditText password = findViewById(R.id.registerPassword);
        TextInputEditText confirm = findViewById(R.id.confirmPassword);
        MaterialAutoCompleteTextView year = findViewById(R.id.yearLevel);
        year.setSimpleItems(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year", "6th Year"});
        TextInputLayout error = findViewById(R.id.confirmPasswordLayout);
        findViewById(R.id.saveStudentButton).setOnClickListener(view -> {
            hideKeyboard();
            String id = value(number), secret = password.getText().toString();
            String level = year.getText().toString();
            if (value(name).isEmpty() || id.isEmpty() || value(program).isEmpty() || level.isEmpty()) {
                error.setError("Complete all student details.");
            } else if (!id.matches("[A-Za-z0-9][A-Za-z0-9-]{3,29}")) {
                error.setError("Use 4-30 letters, numbers or hyphens for your student number.");
            } else if (store.exists(id)) {
                error.setError("This student number already has an account.");
            } else if (secret.length() < 8) {
                error.setError("Use at least 8 characters for your password.");
            } else if (!secret.equals(confirm.getText().toString())) {
                error.setError("Your passwords do not match.");
            } else {
                showProfile(store.register(id, value(name), value(program), level, secret));
            }
        });
        findViewById(R.id.clearStudentButton).setOnClickListener(view -> {
            for (TextInputEditText field : new TextInputEditText[]{name, number, program, password, confirm}) field.setText("");
            year.setText("", false);
            error.setError(null);
        });
        findViewById(R.id.backToLogin).setOnClickListener(view -> {
            hideKeyboard();
            showLogin();
        });
    }

    private void legacyLoginLayout() {
        registering = false;
        page(166);
        heading("Welcome back", "Sign in to view your student information.");
        LinearLayout pageContent = content;
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        pageContent.addView(form);
        content = form;
        TextInputEditText number = field("Student number", false, "e.g. 2024-100001");
        TextInputEditText password = field("Password", true, "Enter your password");
        password.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_DONE);
        status();
        button("Sign in", true, v -> {
            hideKeyboard();
            String id = value(number);
            if (id.isEmpty() || password.getText() == null || password.getText().length() == 0) {
                error("Enter your student number and password."); return;
            }
            StudentStore.Student student = store.login(id, password.getText().toString());
            if (student == null) error("Student number or password is incorrect.");
            else showProfile(student);
        });
        password.setOnEditorActionListener((view, action, event) -> {
            if (action == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                for (int index = 0; index < form.getChildCount(); index++) {
                    View child = form.getChildAt(index);
                    if (child instanceof MaterialButton) { child.performClick(); break; }
                }
                return true;
            }
            return false;
        });
        content = pageContent;
        space(8);
        button("New student? Create an account", false, v -> showRegister());
        space(8);
        View divider = new View(this);
        divider.setBackgroundColor(BORDER);
        content.addView(divider, new LinearLayout.LayoutParams(-1, dp(1)));
        space(4);
        MaterialButton fill = makeButton("Use sample credentials", false);
        fill.setOnClickListener(v -> {
            number.setText("2024-100001"); password.setText("Student@123");
            message.setVisibility(View.GONE);
            fill.setText("Sample filled. Tap Sign in.");
        });
        content.addView(fill, new LinearLayout.LayoutParams(-1, -2));
        footer();
    }

    private void legacyRegistrationLayout() {
        registering = true;
        page(126);
        heading("Create an account", "Enter your student details. All fields are required.");
        TextInputEditText name = field("Student name", false, "Full name");
        name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        TextInputEditText number = field("Student number", false, "e.g. 2024-100002");
        TextInputEditText program = field("Academic program", false, "e.g. BSCS-ML");
        program.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        TextInputLayout yearLayout = inputLayout("Year level");
        yearLayout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
        AutoCompleteTextView year = new MaterialAutoCompleteTextView(this);
        year.setHint("Select your year level");
        year.setMinimumHeight(dp(56));
        year.setPadding(dp(16), dp(16), dp(16), dp(16));
        year.setInputType(InputType.TYPE_NULL);
        year.setTextColor(INK);
        year.setHintTextColor(MUTED);
        year.setTextSize(16);
        year.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year", "6th Year"}));
        yearLayout.addView(year, new LinearLayout.LayoutParams(-1, -2));
        yearLayout.setHintEnabled(false);
        year.setId(View.generateViewId());
        ((TextView) yearLayout.getTag()).setLabelFor(year.getId());
        content.addView(yearLayout);
        space(16);
        TextInputEditText password = field("Password", true, "At least 8 characters");
        TextInputEditText confirm = field("Confirm password", true, "Re-enter your password");
        status();
        button("Create account", true, v -> {
            hideKeyboard();
            String fullName = value(name), id = value(number), course = value(program);
            String level = year.getText().toString().trim();
            String secret = password.getText() == null ? "" : password.getText().toString();
            String repeat = confirm.getText() == null ? "" : confirm.getText().toString();
            if (fullName.isEmpty() || id.isEmpty() || course.isEmpty() || level.isEmpty()) {
                error("Please complete all student information."); return;
            }
            if (!id.matches("[A-Za-z0-9][A-Za-z0-9-]{3,29}")) {
                error("Use 4–30 letters, numbers or hyphens for your student number."); return;
            }
            if (secret.length() < 8) {
                fieldError(password, "Use at least 8 characters."); return;
            }
            if (!secret.equals(repeat)) {
                fieldError(confirm, "Your passwords do not match."); return;
            }
            if (store.exists(id)) { error("This student number already has an account. Sign in instead."); return; }
            StudentStore.Student student = store.register(id, fullName, course, level, secret);
            showProfile(student);
        });
        space(12);
        button("Already registered? Sign in", false, v -> showLogin());
        footer();
    }

    private void showProfile(StudentStore.Student student) {
        registering = false;
        openLayout(R.layout.activity_profile);
        ((TextView) findViewById(R.id.profileName)).setText(student.name);
        ((TextView) findViewById(R.id.profileNumber)).setText(student.number);
        ((TextView) findViewById(R.id.profileProgram)).setText(student.program);
        ((TextView) findViewById(R.id.profileYear)).setText(student.year);
        findViewById(R.id.profileSignOut).setOnClickListener(view -> {
            store.logout();
            showLogin();
        });
    }

    private void legacyProfileLayout(StudentStore.Student student) {
        registering = false;
        page(0);
        heading("My profile", "View your student information and academic record.");
        LinearLayout identity = panel();
        identity.addView(text("Student name", 14, MUTED, false));
        addGap(identity, 6);
        identity.addView(text(student.name, 26, INK, true));
        content.addView(identity);
        space(28);
        content.addView(text("Academic information", 20, INK, true));
        space(16);
        LinearLayout details = panel();
        detail(details, "Student number", student.number, true);
        detail(details, "Academic program", student.program, true);
        detail(details, "Year level", student.year, false);
        content.addView(details);
        space(20);
        button("Sign out", false, v -> {
            store.logout(); showLogin();
        });
        footer();
    }

    private void detail(LinearLayout parent, String label, String value, boolean divider) {
        parent.addView(text(label, 12, MUTED, false));
        addGap(parent, 6);
        parent.addView(text(value, 17, INK, true));
        if (divider) {
            addGap(parent, 18);
            View line = new View(this);
            line.setBackgroundColor(BORDER);
            parent.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
            addGap(parent, 18);
        }
    }

    private TextInputLayout inputLayout(String label) {
        TextInputLayout layout = new TextInputLayout(this, null,
                com.google.android.material.R.attr.textInputOutlinedStyle);
        TextView caption = text(label, 15, INK, false);
        content.addView(caption);
        space(8);
        layout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        layout.setBoxCornerRadii(dp(24), dp(24), dp(24), dp(24));
        layout.setBoxBackgroundColor(SURFACE);
        layout.setBoxStrokeWidth(0);
        layout.setBoxStrokeWidthFocused(dp(2));
        layout.setBoxStrokeColorStateList(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_hovered}, new int[]{}},
                new int[]{BLUE, BLUE, BORDER}));
        layout.setEndIconTintList(ColorStateList.valueOf(MUTED));
        layout.setErrorTextColor(ColorStateList.valueOf(ERROR));
        layout.setTag(caption);
        layout.setDefaultHintTextColor(ColorStateList.valueOf(MUTED));
        layout.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        return layout;
    }

    private TextInputEditText field(String label, boolean password, String placeholder) {
        TextInputLayout layout = inputLayout(label);
        TextInputEditText edit = new TextInputEditText(layout.getContext());
        edit.setSingleLine(true);
        edit.setMinimumHeight(dp(56));
        edit.setImeOptions(android.view.inputmethod.EditorInfo.IME_ACTION_NEXT);
        edit.setTextSize(16);
        edit.setTextColor(INK);
        edit.setHintTextColor(ColorStateList.valueOf(MUTED));
        edit.setPadding(dp(16), dp(16), dp(16), dp(16));
        edit.setInputType(InputType.TYPE_CLASS_TEXT | (password
                ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_VARIATION_NORMAL));
        edit.setTypeface(Typeface.DEFAULT);
        edit.setId(View.generateViewId());
        ((TextView) layout.getTag()).setLabelFor(edit.getId());
        if (placeholder != null) edit.setHint(placeholder);
        if (password) layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        layout.addView(edit, new LinearLayout.LayoutParams(-1, -2));
        layout.setHintEnabled(false);
        content.addView(layout);
        space(16);
        return edit;
    }

    private void status() {
        message = text("", 14, ERROR, false);
        message.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        message.setVisibility(View.GONE);
        content.addView(message);
    }

    private void error(String value) {
        message.setText(value);
        message.setVisibility(View.VISIBLE);
        message.setPadding(0, 0, 0, dp(16));
    }

    private void fieldError(TextInputEditText field, String explanation) {
        if (field.getParent().getParent() instanceof TextInputLayout) {
            ((TextInputLayout) field.getParent().getParent()).setError(explanation);
        } else field.setError(explanation);
        field.requestFocus();
        field.post(() -> field.requestRectangleOnScreen(
                new android.graphics.Rect(0, 0, field.getWidth(), field.getHeight()), false));
    }

    private MaterialButton makeButton(String label, boolean primary) {
        MaterialButton button = new MaterialButton(this);
        button.setText(label);
        button.setTextSize(16);
        button.setAllCaps(false);
        button.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        button.setCornerRadius(dp(28));
        button.setElevation(0);
        button.setStateListAnimator(null);
        button.setBackgroundTintList(ColorStateList.valueOf(primary ? BLUE : Color.TRANSPARENT));
        button.setTextColor(primary ? Color.WHITE : BLUE);
        button.setRippleColor(ColorStateList.valueOf(primary ? 0x33FFFFFF : 0x1918354B));
        ColorStateList focus = new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_focused}, new int[]{}},
                new int[]{primary ? Color.WHITE : BLUE, Color.TRANSPARENT});
        button.setStrokeWidth(dp(2));
        button.setStrokeColor(focus);
        button.setMinimumHeight(dp(primary ? 54 : 48));
        button.setInsetTop(0);
        button.setInsetBottom(0);
        return button;
    }

    private void button(String label, boolean primary, View.OnClickListener action) {
        MaterialButton button = makeButton(label, primary);
        button.setOnClickListener(action);
        content.addView(button, new LinearLayout.LayoutParams(-1, -2));
    }

    private LinearLayout panel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(20), dp(24), dp(20), dp(24));
        panel.setBackground(surface(SURFACE, 18, BORDER));
        panel.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        return panel;
    }

    private GradientDrawable surface(int fill, int radius, int border) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), border);
        return drawable;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(Math.max(12, size));
        view.setTextColor(color);
        view.setTypeface(Typeface.create(bold ? "sans-serif-medium" : "sans-serif", Typeface.NORMAL));
        view.setLineSpacing(dp(3), 1);
        return view;
    }

    private void footer() {
        space(20);
        TextView footer = text("Lab Activity", 12, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        content.addView(footer, new LinearLayout.LayoutParams(-1, -2));
    }

    private String value(TextInputEditText edit) {
        return edit.getText() == null ? "" : edit.getText().toString().trim();
    }
    private void hideKeyboard() {
        View focus = getCurrentFocus();
        if (focus != null) ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }
    private void space(int size) { addGap(content, size); }
    private void addGap(LinearLayout parent, int size) {
        parent.addView(new View(this), new LinearLayout.LayoutParams(1, dp(size)));
    }
    private int dp(int size) { return Math.round(size * getResources().getDisplayMetrics().density); }
}
