# Implementation Plan - Multi-Factor Scoring Engine & Personal Power Meter (Phase 10)

## User Review Required
> [!IMPORTANT]
> **Strict Prerequisite**: All work on Phase 10 is dependent on the completion of the prerequisite refactoring outlined in [sun_indicators_refactoring_implementation_plan.md](file:///C:/MyFiles/Git/AndroidStudioProjects/SacredTimeline/docs/sun_indicators_refactoring_implementation_plan.md). That refactoring must be fully implemented, verified, and committed independently before starting this plan.

---

## Phase 10: Multi-Factor Scoring Engine & Personal Power Meter Implementation Plan

### Milestone 1: Interactive UI Prototype Skeleton & Ergonomics
- **Phase A: Header Power Meter Row (`PowerMeterRow.kt`)**
  - Compact, color-coded status bar with quick action icon buttons (`[🔍 Find Best Window]`, `[⏱️ Preview Time]`, `[📊 Toggle Heatmap Track]`).
- **Phase B: Score Breakdown Sheet (`ScoreBreakdownSheet.kt`, `SpeedometerCanvas.kt`)**
  - Modal bottom sheet featuring an enlarged center-aligned semi-circular speedometer canvas with a needle pointer, score percentage, band title, and itemized factor cards.
- **Phase C: 24-Hour Score Heatmap Track Column (`ScoreHeatmapColumn.kt`)**
  - Optional toggleable vertical gradient column (Red to Green) across the timeline with tap-to-inspect time selection.
- **Phase D: Floating NOW Gauge & Interactive Preview Line (`NowIndicator.kt`, `PreviewIndicator.kt`)**
  - Circular speedometer gauge attached to the NOW line with persistent horizontal drag and vertical slop detection to spawn preview mode.
  - Purple Preview Line with independent vertical drag gesture isolation (`PointerEventPass.Initial`) preventing parent timeline scroll interference.

### Milestone 2: Domain Scoring Engine & Real Wiring
- **Phase E: Core Scoring Engine (`ScoringWeightsConfig.kt`, `ScoringEngine.kt`)**
  - Weighted factor calculation (Thara Bala 20%, Transits 20%, Dasa 20%, Hora 15%, Yoga 15%, Tithi 10% = 100%), rating bands, and warning overrides (Chandrashtamam penalty).
- **Phase F: Wiring & Settings Persistence (`SettingsRepository.kt`)**
  - Connect real scoring engine outputs to UI components and add weight customization preferences in Settings.

### Milestone 3: Verification & Build Check
- Compile debug build: `gradle_build(":app:assembleDebug")`.
- Run automated unit tests verifying scoring bounds and weight normalization.
