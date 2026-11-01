---
name: modularize-large-compose-ui
description: Provides a rigorous, zero-regression workflow and architectural pattern for breaking down monolithic Jetpack Compose UI files (1,000+ lines) into clean, single-responsibility component files (icons, list item renderers, header cards, grid markers, and interactive indicators) with build verification and test suite checks.
---

# Skill: Modularizing Large Jetpack Compose UI Files

## Overview
Monolithic Compose UI files frequently grow beyond 1,000 lines, mixing vector drawings, list item renderers, expandable headers, canvas grid markers, and interactive overlays. This skill defines a phased extraction and regression-audited refactoring protocol.

## Phased Refactoring Protocol

### Phase 1: Custom Icons & Canvas Drawing Extraction (`CustomIcons.kt`)
- Isolate all pure canvas drawing functions, custom vector shapes (e.g., Kalash, Nandi, Shiva, Ganesha, Vel), moon phase calculators, and standalone preview galleries into their own dedicated file.

### Phase 2: Reusable List Item Renderers (`TimelineItemComponents.kt`)
- Extract reusable list item rows, badge renderers, time range formatters (e.g., `"Till 3:45 PM"`), and click callback handlers into a dedicated component file.

### Phase 3: Header Card Containers (`SunTimesDisplay.kt`)
- Isolate top-level expandable summary cards, marquee auto-scrolling row animations, and collapse/expand layout states away from grid rendering.

### Phase 4: Grid Overlay Markers & Indicators (`TimelineIndicators.kt`, `NowIndicator.kt`, `PreviewIndicator.kt`)
- Separate time-offset grid markers (`SunGridMarker`), live time indicators (`NowIndicator`), and touch-isolated inspector preview lines (`PreviewIndicator` with `PointerEventPass.Initial`) into dedicated single-responsibility files.

### Phase 5: Verification & Build Check
- Run compilation checks (`gradle_build(":app:assembleDebug")`) and unit test suites (`app:testDebugUnitTest`) after each extraction phase to ensure absolute zero regressions.
