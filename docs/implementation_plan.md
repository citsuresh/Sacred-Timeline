# Implementation Plan - Phase 2: Natal Hora Compatibility, Background Reminder Reliability, NOW Line Icon Spacing, Sub-Hora UI Polish & Profile Avatars

## Overview
This implementation plan covers Phase 2 of the Sacred Timeline project, incorporating core astrological personalization, background alarm reliability when the app is closed, UI layout adjustments, sub-hora styling, and profile avatar/initials integration.

## Scope of Phase 2 (Ordered by Priority)

1. **Natal Hora Compatibility Engine**:
   - Compute planetary house lordships relative to the active primary birth profile's **Lagna (Ascendant)**.
   - Classify Horas as Favorable (Green) for Trikona lordships (houses 1, 5, 9), Challenging (Red) for Dustana lordships (houses 6, 8, 12), and Neutral (White) for other houses.
   - Fall back to standard weekday-based compatibility when no birth profile is active.

2. **Background Reminder Reliability Fix**:
   - Update `ReminderWorker.scheduleReminder()` to also invoke exact `AlarmManager` (`ReminderReceiver.scheduleReminder()`) with `setExactAndAllowWhileIdle()`, ensuring reminders fire reliably even when the app is closed, killed, or in Doze mode.

3. **NOW Line & Compatibility Icon Spacing / Sizing**:
   - In detail sheets and cards where the "NOW" line/badge intersects timing/sub-hora items, adjust the horizontal layout of the compatibility icon (move slightly to the left with increased padding) and increase icon size so the NOW indicator badge never overlaps or covers it.

4. **Sub-Horas UI Refinement**:
   - Refine sub-horas rendering inside `TimingCard.kt` and `DashboardDetailSheet.kt` to remove heavy rounded pill borders and outer cards, rendering them as clean, seamless time slices integrated flush into the parent Hora card.

5. **Profile Initials & Circular Photo Avatars**:
   - Update `BirthProfile` model with optional `photoUri` (String?) and `avatarColor` (Int / String).
   - Update `ProfileAddDialog.kt` and `ProfileScreen.kt` to allow assigning profile photos or custom avatar colors.
   - Replace generic person icons (`👤`) across the app (top app bar, timeline cards, detail sheets, and widgets) with the active profile's circular-cropped photo or circular initials badge with its distinct background color.

## Proposed Changes by Component

### Model & Data Layer
- **[MODIFY] [BirthProfile.kt](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/app/src/main/java/com/suresh/sacredtimeline/model/BirthProfile.kt)**: Add `photoUri` and `avatarColor` fields.
- **[MODIFY] [PanchangamCalculator.kt](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/app/src/main/java/com/suresh/sacredtimeline/logic/PanchangamCalculator.kt)**: Add natal lordship evaluation based on profile Lagna for Horas.

### Worker & Reminder Layer
- **[MODIFY] [ReminderWorker.kt](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/app/src/main/java/com/suresh/sacredtimeline/worker/ReminderWorker.kt)**: Integrate `ReminderReceiver.scheduleReminder()` and `cancelReminder()` for exact AlarmManager reliability when app is closed.

### UI & Dashboard Layer
- **[MODIFY] [TimingCard.kt](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/app/src/main/java/com/suresh/sacredtimeline/ui/dashboard/components/TimingCard.kt)**: Update Hora cards with profile avatar/initials indicator, borderless/seamless sub-horas styling, and icon spacing for NOW line.
- **[MODIFY] [DashboardDetailSheet.kt](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/app/src/main/java/com/suresh/sacredtimeline/ui/dashboard/components/DashboardDetailSheet.kt)**: Update detail sheet with profile avatar/initials, refined sub-horas breakdown, and padded/larger compatibility icon to avoid NOW badge overlap.
- **[MODIFY] [PanchangamWidget.kt](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/app/src/main/java/com/suresh/sacredtimeline/widget/PanchangamWidget.kt)**: Reflect natal compatibility and profile indicator in Glance widget.

### Profile & App Bar Layer
- **[MODIFY] [ProfileAddDialog.kt](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/app/src/main/java/com/suresh/sacredtimeline/ui/profile/ProfileAddDialog.kt)**: Add avatar color picker / photo picker support.
- **[MODIFY] [ProfileScreen.kt](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/app/src/main/java/com/suresh/sacredtimeline/ui/profile/ProfileScreen.kt)**: Render profile avatars / initials badges.
- **[MODIFY] [MainActivity.kt](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/app/src/main/java/com/suresh/sacredtimeline/MainActivity.kt)**: Update top app bar profile action icon to display active profile's circular photo or initials badge.

## Verification Plan
### Automated Tests
- Run unit tests: `gradle_build(":app:testDebugUnitTest")`.
- Build debug APK: `gradle_build(":app:assembleDebug")`.

### Manual Verification
- Deploy app to emulator/device, add a profile with initials/avatar color/photo, switch active profile, and verify:
  1. Top app bar and timeline cards display the correct profile avatar/initials badge.
  2. Hora compatibility reflects natal Lagna Trikona/Dustana lordships.
  3. Sub-horas render cleanly without heavy outer/rounded borders.
  4. Compatibility icons in detail sheets are larger and positioned well to the left so the NOW badge does not overlap them.
  5. Reminders fire reliably even when the app is closed.
