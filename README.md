# Student Information Application

A native Java Android Application with National University-inspired navy blue and yellow interface. It is NOT an official NU application.

## Screens
- **Sign in:** student number and password, password visibility toggle, and sample credential helper.
- **Registration:** student name, unique student number, academic program (for example `BSCS-ML`), year level, password and confirmation. Successful registration signs the student in.
- **Student profile:** displays all four student details, with a sign-out action. Students can edit their name, academic program, and year level; their student number is read-only. Edits and sessions persist on the device.
- **Class schedule:** the profile opens the sample timetable on the current day. Monday, Tuesday, Thursday, and Friday have four classes each; Wednesday, Saturday, and Sunday are free.

## Sample login
- Student number: `2024-100001`
- Password: `Student@123`
- Student: Jon Bell / BSCS-ML / 3rd Year

## Run
Open this project in Android Studio, sync Gradle, then run the `app` configuration on an emulator or device running Android 7.0 (API 24) or later.

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest lintDebug
# With an emulator or device connected:
.\gradlew.bat connectedDebugAndroidTest
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.
