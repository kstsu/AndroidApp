# Student Information Application

A native Java Android demo with a minimal National University-inspired navy blue and yellow interface. It is not an official NU application.

## Screens
- **Sign in:** student number and password, password visibility toggle, and sample credential helper.
- **Registration:** student name, unique student number, academic program (for example `BSCS-ML`), year level, password and confirmation. Successful registration signs the student in.
- **Student profile:** displays all four student details, with a sign-out action. Sessions persist across app restarts.

## Sample login
- Student number: `2024-100001`
- Password: `Student@123`
- Student: Alexandra Reyes / BSCS-ML / 2nd Year

## Run
Open this project in Android Studio, sync Gradle, then run the `app` configuration on an emulator or device running Android 7.0 (API 24) or later.

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest lintDebug
# With an emulator or device connected:
.\gradlew.bat connectedDebugAndroidTest
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Demo storage and security
Accounts and the current session are stored in app-private SharedPreferences. Passwords use PBKDF2 with individual random salts, not plaintext. Registration checks required fields, duplicate student numbers, minimum password length, and matching confirmation. Student numbers are case-insensitive.

This is an offline teaching/demo application, not production university authentication. Clearing app data or uninstalling removes registered accounts. A production system needs server-side authentication, authorization, secure token management, recovery, rate limiting, and a university-backed student database. The published sample password must not be used in production.
