# New Feature & Column Checklist

This checklist MUST be completed whenever a new timing slot, traditional period, or UI column is added to the Sacred Timeline to ensure "Full Pipeline" synchronization.

## 1. Logic & Data
- [ ] Added to `DayData` model.
- [ ] Calculation logic implemented in `DayDataProvider` (SSOT for App & Widget).
- [ ] Included in `CacheManager` (if serialization changes are needed).

## 2. Main App UI
- [ ] Added as a lane/column in `TimelineCore.kt`.
- [ ] Respects scaling and "Harmony" layout rules (no overlapping boxes).
- [ ] Localized strings added to `strings.xml` and `strings.xml (ta)`.
- [ ] Auspiciousness colors assigned in `SacredTimelineColors`.

## 3. Widget Synchronization
- [ ] UI representation added to `PanchangamWidget.kt` (Universal and/or specific column).
- [ ] **Transition Boundaries**: Start and End times added to `WidgetUpdateWorker.scheduleNextTransition`.
- [ ] Immediate update triggered in `TimelineViewModel` upon relevant setting change.

## 4. User Control & Settings
- [ ] Visibility toggle added to `SettingsRepository`.
- [ ] Toggle added to `SettingsScreen` (with appropriate Material 3 icon).
- [ ] Added to `VerifiedHolidays` or `RitualContext` if it's a date-anchored event.

## 5. Reminders & Notifications Integration
- [ ] **Prompt User**: Explicitly ask the user if the feature requires reminder/notification support.
- [ ] **Category & Channel 1-to-1 Sync**: Add the category to `ReminderDialog` / `AddGenericReminderDialog` and create its dedicated notification channel in `SacredTimelineApp.kt` & `ReminderWorker.kt`.
- [ ] **Background Execution**: Ensure scheduling via WorkManager (`ReminderWorker`) for OEM reliability.

## 6. Localization & Translation Audit
- [ ] **Zero Hardcoded Strings**: Verify all UI labels, titles, buttons, and descriptions use `stringResource(R.string...)`.
- [ ] **Bilingual Parity**: Verify every new string added to `values/strings.xml` has an accurate Tamil translation in `values-ta/strings.xml` (including descriptions, metadata, guidance, and activity lists).

## 7. Regression Audit
- [ ] Verified by Regression Auditor sub-agent using the **Feature Parity Checklist** in `AGENTS.md`.
