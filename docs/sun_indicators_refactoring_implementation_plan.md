# Implementation Plan - SunIndicators.kt Modularization Refactoring (Prerequisite)

## User Review Required
> [!IMPORTANT]
> This document outlines the rigorous, zero-regression phased approach for refactoring the monolithic `SunIndicators.kt` file. It includes a complete impact analysis, step-by-step extraction phases, and a manual testing checklist to guarantee absolute stability.

---

## 1. Impact Analysis: What Features Depend on `SunIndicators.kt`?
- **Daily Summary Header (Calendar Row & Marquee)**: Tamil date, Paksha, Tithis, Nakshatras, festivals, and special events.
- **Sun & Muhurtham Row**: Sunrise, Sunset, Brahma, Abhijit, and Maitra Muhurtham indicators and click handlers.
- **Header Expansion**: Swipe down / tap expand gesture for wrapping event chips (`FlowRow`).
- **NOW Line & Gauge**: Live time marker and score badge on the 24-hour vertical timeline.
- **Preview Line (Inspector Mode)**: Purple preview line, scrubber, and score indicator.

---

## 2. Rigorous Phased Refactoring Approach

### Phase 1: Extract `SpeedometerCanvas.kt`
- Create `SpeedometerCanvas.kt` housing the shared speedometer drawing logic.
- **Verification**: Run `gradle_build(":app:assembleDebug")`.

### Phase 2: Extract `NowIndicator.kt`
- Create `NowIndicator.kt` housing the NOW line and circular badge.
- **Verification**: Run `gradle_build(":app:assembleDebug")`. Check NOW line rendering on today's view.

### Phase 3: Extract `PreviewIndicator.kt`
- Create `PreviewIndicator.kt` housing the purple preview line and touch-isolated vertical dragging.
- **Verification**: Run `gradle_build(":app:assembleDebug")`. Check preview line spawning and dragging.

### Phase 4: Streamline `SunIndicators.kt`
- Keep `SunTimesDisplay`, `HeaderItems`, and marquee components cleanly structured in `SunIndicators.kt`.
- **Verification**: Run `gradle_build(":app:assembleDebug")`.

---

## 3. Manual Regression Testing Checklist
Before considering the refactoring complete and proceeding to Phase 10, manually verify:
1. [ ] **Header Collapse/Expansion**: Tap or swipe down on the header; verify smooth transition between marquee and expanded `FlowRow`.
2. [ ] **Sun & Muhurtham Row**: Verify Sunrise/Sunset times and Muhurtham marquees display correctly. Tapping them opens `DashboardDetailSheet`.
3. [ ] **NOW Line & Gauge**: Verify the NOW line appears at the correct time offset on today's date with the circular gauge. Tapping it opens `ScoreBreakdownSheet`.
4. [ ] **Preview Line**: Verify vertical drag spawns the purple preview line and allows smooth vertical scrubbing.
5. [ ] **Multi-Day Navigation**: Swipe across different dates; verify zero crashes, correct date formatting, and smooth pager synchronization.
