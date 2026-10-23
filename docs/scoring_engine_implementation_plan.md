# Implementation Plan - Multi-Factor Scoring Engine & Personal Power Meter

## User Review Required
> [!IMPORTANT]
> This plan outlines the implementation of the **Multi-Factor Scoring Engine & Personal Power Meter** (Phase 11), featuring a real-time **"Now Score"** (0-100%), human-friendly interpretation bands, Chandrashtamam/malefic Tara Bala penalty overrides, transparent factor breakdown audit view, user-configurable weights in Settings, and strict bilingual localization parity (English & Tamil).

## Proposed Components & Changes

### 1. Domain Logic & Scoring Engine (`ScoringEngine.kt`, `ScoringWeightsConfig.kt`)
- **Scope**:
  - Create `ScoringWeightsConfig` data model (Thara Bala 15%, Chandrashtamam/Afflictions 15%, Transit 15%, Dasa 15%, Hora 15%, Yoga 15%, Tithi 10% = 100%).
  - Implement `ScoringEngine` calculator function taking `DayData`, `BirthProfile`, current `LocalTime`, and weights.
  - Implement rating band mapping (`Very Favorable`, `Favorable`, `Mixed`, `Challenging`, `Avoid`) and color mappings.
  - Implement critical personal warning override (e.g., active Chandrashtamam caps score at 35%).
  - Generate itemized score breakdown points for transparency audit.

### 2. Settings & Persistence (`SettingsRepository.kt`, `ScoringSettingsScreen.kt`)
- **Scope**:
  - Add DataStore preferences for custom scoring weights in `SettingsRepository.kt`.
  - Build UI controls in Settings allowing users to customize weight percentages with real-time total validation (summing to 100%).

### 3. Dashboard UI & Power Meter Component (`PowerMeterCard.kt`, `ScoreBreakdownDialog.kt`, `TimelineDashboard.kt`)
- **Scope**:
  - Build `PowerMeterCard` (circular arc gauge showing percentage, band title, and color status) placed prominently above the timeline.
  - Build `ScoreBreakdownDialog` to display transparent factor-by-factor contributions upon tapping the gauge.
  - Wire up live score updates as time transitions or date is selected.

### 4. Localization & Translation Parity (`strings.xml`, `values-ta/strings.xml`)
- **Scope**:
  - Externalize all rating bands (`Very Favorable`, `Favorable`, `Mixed`, `Challenging`, `Avoid`), breakdown titles, and weight labels into `values/strings.xml` and complete fluent Tamil translations in `values-ta/strings.xml`.

### 5. Verification Plan
- **Automated Tests**:
  - Unit tests verifying scoring bounds (0-100), Chandrashtamam penalty overrides, and weight normalization.
- **Build Verification**:
  - Compile debug build: `gradle_build(":app:assembleDebug")`.
