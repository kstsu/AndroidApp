package com.example.studentinfoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Device-local demo accounts. A production app should authenticate with a backend. */
final class StudentStore {
    private static final String SESSION = "active_student";
    private final SharedPreferences prefs;

    StudentStore(Context context) {
        prefs = context.getSharedPreferences("student_accounts", Context.MODE_PRIVATE);
        if (!exists("2024-100001")) {
            save("2024-100001", "Alexandra Reyes", "BSCS-ML", "2nd Year", "Student@123");
        }
    }

    boolean exists(String number) { return prefs.contains(key(number)); }

    Student register(String number, String name, String program, String year, String password) {
        if (exists(number)) throw new IllegalArgumentException("Student already exists");
        Student student = save(number, name, program, year, password);
        prefs.edit().putString(SESSION, key(number)).apply();
        return student;
    }

    Student login(String number, String password) {
        try {
            JSONObject account = new JSONObject(prefs.getString(key(number), "{}"));
            byte[] salt = Base64.decode(account.getString("salt"), Base64.NO_WRAP);
            byte[] expected = Base64.decode(account.getString("hash"), Base64.NO_WRAP);
            if (!MessageDigest.isEqual(expected, derive(password, salt))) return null;
            prefs.edit().putString(SESSION, key(number)).apply();
            return student(account);
        } catch (JSONException | IllegalArgumentException exception) {
            return null;
        }
    }

    Student currentStudent() {
        String key = prefs.getString(SESSION, null);
        if (key == null) return null;
        try { return student(new JSONObject(prefs.getString(key, "{}"))); }
        catch (JSONException exception) { logout(); return null; }
    }

    void logout() { prefs.edit().remove(SESSION).apply(); }

    private Student save(String number, String name, String program, String year, String password) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            JSONObject account = new JSONObject();
            account.put("number", number).put("name", name).put("program", program).put("year", year);
            account.put("salt", Base64.encodeToString(salt, Base64.NO_WRAP));
            account.put("hash", Base64.encodeToString(derive(password, salt), Base64.NO_WRAP));
            prefs.edit().putString(key(number), account.toString()).apply();
            return student(account);
        } catch (JSONException exception) { throw new IllegalStateException(exception); }
    }

    private static byte[] derive(String password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 120000, 256);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).getEncoded(); }
        catch (java.security.GeneralSecurityException exception) { throw new IllegalStateException(exception); }
        finally { spec.clearPassword(); }
    }

    private static String key(String number) { return "student_" + number.trim().toLowerCase(Locale.ROOT); }
    private static Student student(JSONObject data) throws JSONException {
        return new Student(data.getString("number"), data.getString("name"),
                data.getString("program"), data.getString("year"));
    }

    static final class Student {
        final String number, name, program, year;
        Student(String number, String name, String program, String year) {
            this.number = number; this.name = name; this.program = program; this.year = year;
        }
    }
}
