# Student portal design

## Reference and scope
The supplied dark authentication-screen reference supersedes the previous light design. The layout is adapted to the existing student portal, retaining navy and yellow rather than switching to the reference's lime green. Authentication, registration, profile fields and local persistence are unchanged. No simulated social sign-in, email recovery or biometric actions are included.

## Visual system
- Canvas: deep navy `#090F1C`, with a restrained navy tint at the top.
- Surface: `#0F1829`; border: `#36445A`.
- Primary text: `#F2F5FC`; secondary: `#A9B4C8`; action: `#FFCF40`.
- Static vector NU orb with a fine gold orbit replaces the square logo. It needs no image download or animation.
- Centered titles, external input labels, 14dp outlined inputs, 54dp rounded gold primary buttons.
- Login has no enclosing card. Secondary navigation stays separate from the primary action.
- Registration and profile share the same dark visual language; student details remain scrollable.
- Horizontal gutters: 24dp; maximum column width: 520dp for tablets.
- Footer remains exactly `Lab Activity`.

## Interaction and accessibility
Native password toggles and year-level dropdown. White status-bar icons, explicit form labels, visible gold input focus, light-red error text, at least 16sp input text and 54dp actions. IME Done submits login. System-bar and keyboard insets are respected. Larger text can wrap and pages scroll; no fixed full-screen content height.

## Verification
Build, unit tests and lint run with Android Studio's installed JDK. Native instrumentation tests cover account persistence and login/registration view creation and navigation. These are not substitutes for full accessibility or multi-device testing.
