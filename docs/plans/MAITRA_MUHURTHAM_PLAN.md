# Active Implementation Plan: Maitra Muhurtham (Debt Repayment)

**Current Status**: Discovery Complete / Ready for Implementation
**Linked Project State**: [PROJECT_STATE.md](../PROJECT_STATE.md)

This document tracks the progress of the **Maitra Muhurtham** engine and UI integration.

## 1. Feature Definition
Maitra Muhurtham (மைத்ர முஹூர்த்தம்) is a high-precision astrological window for debt repayment.
- **Goal**: Add dynamic calculation of these windows based on user GPS and local Lagna.
- **Potency**: 
    - 5 Stars (⭐⭐⭐⭐⭐): Tuesday + (Ashwini/Mesha OR Anuradha/Scorpio).
    - 3 Stars (⭐⭐⭐): Other Weekday + (Ashwini/Mesha OR Anuradha/Scorpio).

## 2. Task Checklist

- `[ ]` **Phase 1: Logic & Math**
    - `[ ]` Implement `LagnaCalculator.kt` using Sidereal Sun Longitude.
    - `[ ]` Add `MaitraMuhurtham` detection logic in `TimelineViewModel`.
    - `[ ]` Verify against known 2026 dates (Feb 10, Jul 24, etc.) via Unit Tests.
- `[ ]` **Phase 2: Data Wiring**
    - `[ ]` Add `MaitraMuhurtham` data class to `Timings.kt`.
    - `[ ]` Integrate into `DayData` and register `MAITRA` as a valid column ID.
- `[ ]` **Phase 3: UI Implementation**
    - `[ ]` Define Gold color palette in `SacredTimelineColors.kt`.
    - `[ ]` Update `TimingCard.kt` to handle 3/5 star display and Gold backgrounds.
    - `[ ]` Add `MAITRA` column to the Universal View sorting logic.
- `[ ]` **Phase 4: Localization & Settings**
    - `[ ]` Add Tamil/English strings for "High Potency" and Lagna combinations.
    - `[ ]` Update `SettingsRepository` to include Maitra column toggles.

## 3. Reference Verification List (Coimbatore 2026)
| Date | Potency | Lagna Window |
| :--- | :--- | :--- |
| Feb 10 | ⭐⭐⭐⭐⭐ | 03:42 AM – 05:54 AM |
| Jul 24 | ⭐⭐⭐ | 01:50 PM – 04:05 PM |
| Aug 04 | ⭐⭐⭐⭐⭐ | 09:34 PM – 11:46 PM |

## 4. Progress Log
- **2026-08-08**: Research complete. Implementation plan finalized and moved to persistent storage.
