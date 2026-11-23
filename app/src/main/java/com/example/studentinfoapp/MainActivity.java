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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
    private static final int BLUE = Color.rgb(28, 49, 110);
    private static final int YELLOW = Color.rgb(255, 207, 64);
    private static final int INK = Color.rgb(28, 37, 57);
    private static final int MUTED = Color.rgb(108, 116, 133);
    private static final int CANVAS = Color.rgb(247, 246, 242);
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

    private void page() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CANVAS);
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
        content.setPadding(dp(24), dp(16), dp(24), dp(28));
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, -1));
        setContentView(root);
        ViewCompat.getWindowInsetsController(root).setAppearanceLightStatusBars(true);
        ViewCompat.getWindowInsetsController(root).setAppearanceLightNavigationBars(true);

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setGravity(Gravity.CENTER);
        TextView mark = text("NU", 24, BLUE, true);
        mark.setGravity(Gravity.CENTER);
        mark.setLetterSpacing(-0.04f);
        mark.setBackground(surface(YELLOW, 12, YELLOW));
        brand.addView(mark, new LinearLayout.LayoutParams(dp(56), dp(56)));
        addGap(brand, 12);
        TextView name = text("Student Portal", 18, BLUE, true);
        name.setGravity(Gravity.CENTER);
        brand.addView(name);
        content.addView(brand, new LinearLayout.LayoutParams(-1, -2));
        space(28);
    }

    private void heading(String eyebrow, String title, String subtitle) {
        TextView section = text(eyebrow, 12, BLUE, true);
        section.setLetterSpacing(0.06f);
        content.addView(section);
        space(10);
        TextView display = text(title, 38, BLUE, false);
        display.setTypeface(Typeface.create("serif", Typeface.NORMAL));
        display.setLetterSpacing(-0.035f);
        ViewCompat.setAccessibilityHeading(display, true);
        content.addView(display);
        space(8);
        content.addView(text(subtitle, 16, MUTED, false));
        space(28);
    }

    private void showLogin() {
        registering = false;
        page();
        TextView title = text("Welcome back", 30, BLUE, true);
        title.setGravity(Gravity.CENTER);
        ViewCompat.setAccessibilityHeading(title, true);
        content.addView(title, new LinearLayout.LayoutParams(-1, -2));
        space(8);
        TextView description = text("Sign in to your student account.", 16, MUTED, false);
        description.setGravity(Gravity.CENTER);
        content.addView(description, new LinearLayout.LayoutParams(-1, -2));
        space(28);
        LinearLayout pageContent = content;
        LinearLayout form = panel();
        form.setBackground(surface(Color.WHITE, 16, Color.rgb(230, 231, 235)));
        pageContent.addView(form);
        content = form;
        TextInputEditText number = field("Student number", false, "2024-100001");
        TextInputEditText password = field("Password", true, null);
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
        space(16);
        button("New student? Create an account", false, v -> showRegister());
        space(20);
        LinearLayout demo = panel();
        demo.addView(text("Try a sample account", 16, BLUE, true));
        addGap(demo, 8);
        demo.addView(text("Student number: 2024-100001\nPassword: Student@123", 13, MUTED, false));
        MaterialButton fill = makeButton("Use sample credentials", false);
        fill.setOnClickListener(v -> {
            number.setText("2024-100001"); password.setText("Student@123");
            fill.setText("Sample filled · Sign in above");
            number.requestFocus();
            number.post(() -> number.requestRectangleOnScreen(
                    new android.graphics.Rect(0, 0, number.getWidth(), number.getHeight()), false));
        });
        demo.addView(fill, new LinearLayout.LayoutParams(-1, dp(52)));
        content.addView(demo);
        footer();
    }

    private void showRegister() {
        registering = true;
        page();
        button("Back to sign in", false, v -> showLogin());
        space(16);
        heading("CREATE YOUR RECORD", "Make it yours.",
                "Create an account with your student details.");
        content.addView(text("Student details / All fields required", 14, BLUE, true));
        space(20);
        TextInputEditText name = field("Student name", false, "Full name");
        name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        TextInputEditText number = field("Student number", false, "e.g. 2024-100002");
        TextInputEditText program = field("Academic program", false, "e.g. BSCS-ML");
        program.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        TextInputLayout yearLayout = inputLayout("Year level");
        yearLayout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
        AutoCompleteTextView year = new AutoCompleteTextView(this);
        year.setInputType(InputType.TYPE_NULL);
        year.setTextColor(INK);
        year.setTextSize(16);
        year.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year", "5th Year", "6th Year"}));
        yearLayout.addView(year, new LinearLayout.LayoutParams(-1, -2));
        content.addView(yearLayout);
        space(16);
        content.addView(text("Account security", 20, BLUE, true));
        space(8);
        content.addView(text("Choose a password with at least 8 characters.", 14, MUTED, false));
        space(20);
        TextInputEditText password = field("Password", true, "At least 8 characters");
        TextInputEditText confirm = field("Confirm password", true, null);
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
        page();
        heading("MY STUDENT RECORD", "Hello, " + student.name.split(" ")[0] + ".",
                "Your information, all in one place.");
        LinearLayout identity = panel();
        identity.setBackground(surface(BLUE, 12, BLUE));
        identity.addView(text("NU / STUDENT PROFILE", 12, YELLOW, true));
        addGap(identity, 12);
        identity.addView(text(student.name, 28, Color.WHITE, true));
        addGap(identity, 8);
        TextView studentId = text(student.number, 17, Color.WHITE, false);
        studentId.setTypeface(Typeface.MONOSPACE);
        identity.addView(studentId);
        addGap(identity, 28);
        identity.addView(text(student.program + "  /  " + student.year, 16, YELLOW, true));
        content.addView(identity);
        space(28);
        content.addView(text("Academic information", 20, INK, true));
        space(16);
        LinearLayout details = panel();
        detail(details, "Student number", student.number, true);
        detail(details, "Student name", student.name, true);
        detail(details, "Academic program", student.program, true);
        detail(details, "Year level", student.year, false);
        content.addView(details);
        space(20);
        content.addView(text("These are the details associated with your account.", 13, MUTED, false));
        space(28);
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
            line.setBackgroundColor(Color.rgb(232, 235, 241));
            parent.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
            addGap(parent, 18);
        }
    }

    private TextInputLayout inputLayout(String label) {
        TextInputLayout layout = new TextInputLayout(this, null,
                com.google.android.material.R.attr.textInputOutlinedStyle);
        layout.setHint(label);
        layout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        layout.setBoxCornerRadii(dp(8), dp(8), dp(8), dp(8));
        layout.setBoxBackgroundColor(Color.WHITE);
        layout.setBoxStrokeColor(BLUE);
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
        edit.setInputType(InputType.TYPE_CLASS_TEXT | (password
                ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_VARIATION_NORMAL));
        edit.setId(View.generateViewId());
        if (placeholder != null) layout.setPlaceholderText(placeholder);
        if (password) layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        layout.addView(edit, new LinearLayout.LayoutParams(-1, -2));
        content.addView(layout);
        space(16);
        return edit;
    }

    private void status() {
        message = text("", 13, Color.rgb(165, 39, 39), false);
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
        field.setError(explanation);
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
        button.setCornerRadius(dp(8));
        button.setElevation(0);
        button.setStateListAnimator(null);
        button.setBackgroundTintList(ColorStateList.valueOf(primary ? BLUE : Color.TRANSPARENT));
        button.setTextColor(primary ? Color.WHITE : BLUE);
        button.setRippleColor(ColorStateList.valueOf(primary ? 0x33FFCF40 : 0x181C316E));
        button.setMinimumHeight(dp(54));
        button.setInsetTop(0);
        button.setInsetBottom(0);
        return button;
    }

    private void button(String label, boolean primary, View.OnClickListener action) {
        MaterialButton button = makeButton(label, primary);
        button.setOnClickListener(action);
        content.addView(button, new LinearLayout.LayoutParams(-1, dp(54)));
    }

    private LinearLayout panel() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(20), dp(24), dp(20), dp(24));
        panel.setBackground(surface(Color.rgb(239, 238, 232), 10, Color.rgb(239, 238, 232)));
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
        space(32);
        TextView footer = text("Lab Activity", 12, MUTED, false);
        footer.setGravity(Gravity.CENTER);
        content.addView(footer, new LinearLayout.LayoutParams(-1, -2));
    }

    private void rule(LinearLayout parent) {
        View line = new View(this);
        line.setBackgroundColor(0x331C316E);
        parent.addView(line, new LinearLayout.LayoutParams(-1, dp(1)));
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
