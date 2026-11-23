# Student portal visual direction

Native Java Android, retaining the existing login, registration and local student store.

The requested Gibous reference informs editorial hierarchy, small catalog-style section labels, understated surfaces and crisp rectangular controls. It does not introduce shopping features, copy the store's branding, or replace the NU-inspired navy and gold identity.

- Canvas: warm paper `#F7F6F2`; navy `#1C316E`; gold `#FFCF40`.
- Large serif screen titles contrast with native sans-serif controls.
- Shared 24dp horizontal content gutter and 54dp primary actions.
- Text floor: 12sp; inputs and action labels: 16sp.
- Profile identity uses navy with white text and gold academic metadata.
- Primary actions remain explicit: sign in, create account, sign out.
- Input focus, password visibility, keyboard resizing and scroll access remain native.
- Header: centered yellow NU monogram with a simple “Student Portal” label. No editorial wordmark or utility strip.
- Login: centered welcome title, white form surface, student number and password fields, one navy sign-in action, separate registration link and sample-account helper. Keyboard Done submits the form.
- Modern refinement: 8dp controls, 10dp neutral panels, 12dp student identity card, text-only secondary actions. No decorative motion.
- Footer copy is exactly “Lab Activity”, without the previous disclaimer or divider. The README retains the demo context.
- Password validation points to the affected field; invalid credentials use a non-specific error.

Relevant source guidelines: the attached `skill2/READFIRST.md` and `skill2/frontend/FRONTEND.md`. Browser-specific audit tooling does not apply to these native Android views. Visual/device testing still requires a connected emulator or phone.
