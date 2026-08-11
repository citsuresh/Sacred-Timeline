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

## 5. Regression Audit
- [ ] Verified by Regression Auditor sub-agent using the **Feature Parity Checklist** in `AGENTS.md`.
