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
    private static final int CANVAS = Color.rgb(248, 249, 252);
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
        content.setPadding(dp(28), dp(28), dp(28), dp(28));
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, -1));
        setContentView(root);
        ViewCompat.getWindowInsetsController(root).setAppearanceLightStatusBars(true);
        ViewCompat.getWindowInsetsController(root).setAppearanceLightNavigationBars(true);

        LinearLayout brand = new LinearLayout(this);
        brand.setGravity(Gravity.CENTER_VERTICAL);
        TextView mark = text("NU", 20, BLUE, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(surface(YELLOW, 10, YELLOW));
        brand.addView(mark, new LinearLayout.LayoutParams(dp(48), dp(48)));
        TextView name = text("STUDENT PORTAL\nYour academic companion", 12, BLUE, true);
        name.setPadding(dp(14), 0, 0, 0);
        brand.addView(name);
        content.addView(brand);
        space(40);
    }

    private void heading(String eyebrow, String title, String subtitle) {
        content.addView(text(eyebrow, 11, BLUE, true));
        space(10);
        content.addView(text(title, 34, INK, true));
        space(8);
        content.addView(text(subtitle, 15, MUTED, false));
        space(28);
    }

    private void showLogin() {
        registering = false;
        page();
        heading("WELCOME BACK", "A place for\nyour next chapter.",
                "Sign in to view your student information.");
        TextInputEditText number = field("Student number", false, "2024-100001");
        TextInputEditText password = field("Password", true, null);
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
        space(12);
        button("New student? Create an account", false, v -> showRegister());
        space(28);
        LinearLayout demo = panel();
        demo.addView(text("JUST EXPLORING?", 11, BLUE, true));
        addGap(demo, 8);
        demo.addView(text("Try the sample student account", 15, INK, true));
        addGap(demo, 8);
        demo.addView(text("Student number: 2024-100001\nPassword: Student@123", 13, MUTED, false));
        MaterialButton fill = makeButton("Use sample credentials", false);
        fill.setOnClickListener(v -> {
            number.setText("2024-100001"); password.setText("Student@123");
        });
        demo.addView(fill, new LinearLayout.LayoutParams(-1, dp(52)));
        content.addView(demo);
        footer();
    }

    private void showRegister() {
        registering = true;
        page();
        heading("YOUR JOURNEY STARTS HERE", "Make it yours.",
                "Create an account with your student details.");
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
            if (secret.length() < 8) { error("Use a password with at least 8 characters."); return; }
            if (!secret.equals(repeat)) { error("Your passwords do not match."); return; }
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
        heading("MY STUDENT SPACE", "Hello, " + student.name.split(" ")[0] + ".",
                "Your information, all in one place.");
        LinearLayout identity = panel();
        View accent = new View(this);
        accent.setBackgroundColor(YELLOW);
        identity.addView(accent, new LinearLayout.LayoutParams(dp(36), dp(5)));
        addGap(identity, 20);
        identity.addView(text("STUDENT PROFILE", 11, BLUE, true));
        addGap(identity, 12);
        identity.addView(text(student.name, 26, BLUE, true));
        addGap(identity, 8);
        identity.addView(text(student.number, 16, MUTED, false));
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
        layout.setBoxCornerRadii(dp(10), dp(10), dp(10), dp(10));
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

    private MaterialButton makeButton(String label, boolean primary) {
        MaterialButton button = new MaterialButton(this);
        button.setText(label);
        button.setTextSize(14);
        button.setAllCaps(false);
        button.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        button.setCornerRadius(dp(10));
        button.setElevation(0);
        button.setStateListAnimator(null);
        button.setBackgroundTintList(ColorStateList.valueOf(primary ? BLUE : Color.TRANSPARENT));
        button.setTextColor(primary ? Color.WHITE : BLUE);
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
        panel.setPadding(dp(22), dp(22), dp(22), dp(22));
        panel.setBackground(surface(Color.WHITE, 12, Color.rgb(231, 234, 241)));
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
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTypeface(Typeface.create(bold ? "sans-serif-medium" : "sans-serif", Typeface.NORMAL));
        view.setLineSpacing(dp(3), 1);
        return view;
    }

    private void footer() {
        space(32);
        TextView footer = text("STUDENT INFORMATION\nLocal demo · Inspired by NU blue & gold", 11, MUTED, false);
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
