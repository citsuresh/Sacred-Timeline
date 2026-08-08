# Active Implementation Plan: Maitra Muhurtham (Debt Repayment)

**Current Status**: Feature Complete & Verified
**Linked Project State**: [PROJECT_STATE.md](../PROJECT_STATE.md)

This document tracks the progress of the **Maitra Muhurtham** engine and UI integration.

## 1. Feature Definition
Maitra Muhurtham (மைத்ர முஹூர்த்தம்) is a high-precision astrological window for debt repayment.
- **Goal**: Add dynamic calculation of these windows based on user GPS and local Lagna.
- **Potency**: 
    - 5 Stars (⭐⭐⭐⭐⭐): Tuesday + (Ashwini/Mesha OR Anuradha/Scorpio).
    - 3 Stars (⭐⭐⭐): Other Weekday + (Ashwini/Mesha OR Anuradha/Scorpio).

## 2. Task Checklist

- `[x]` **Phase 1: Logic & Math**
    - `[x]` Implement `LagnaCalculator.kt` using IAU 1982 / Meeus Sidereal Longitude.
    - `[x]` Add `MaitraMuhurtham` detection logic in `PanchangamCalculator`.
    - `[x]` Verify against known 2026 dates (Feb 11, Jul 24, Aug 04) via Unit Tests.
- `[x]` **Phase 2: Data Wiring**
    - `[x]` Add `MaitraMuhurtham` data class to `Timings.kt`.
    - `[x]` Integrate into `DayData` and register `MAITRA` as a valid column ID.
- `[x]` **Phase 3: UI Implementation**
    - `[x]` Define Gold color palette in `SacredTimelineColors.kt` (Bright vs Pale).
    - `[x]` Update `TimingCard.kt` to handle 3/5 star display and explicit potency labels.
    - `[x]` Add `MAITRA` column to the Universal View sorting logic (Center Track).
- `[x]` **Phase 4: Localization & Settings**
    - `[x]` Add Tamil/English strings for "High Potency" and "Standard Potency".
    - `[x]` Update `SettingsRepository` to include Maitra column toggles by default.
- `[x]` **Phase 5: Regression & Polish**
    - `[x]` Consolidate Lahiri Ayanamsha into shared utility.
    - `[x]` Implement Vara-based potency logic (Sunrise-anchored continuity).
    - `[x]` Integrated Maitra support into Home Screen Widget.

## 3. Reference Verification List (Coimbatore 2026)
| Date | Potency | Engine Result (Approx) | Status |
| :--- | :--- | :--- | :--- |
| Feb 11 | ⭐⭐⭐⭐⭐ | 02:27 AM – 04:44 AM | ✅ PASSED |
| Jul 24 | ⭐⭐⭐ | 03:14 PM – 05:32 PM | ✅ PASSED |
| Aug 04 | ⭐⭐⭐⭐⭐ | 10:51 PM – 12:43 AM (next day) | ✅ PASSED |

## 4. Progress Log
- **2026-08-08**: Research complete. Implementation plan finalized.
- **2026-08-08**: Full stack implementation completed. High-precision astronomical drift corrected via Ayanamsha consolidation. UI polished with dual-tone gold and explicit potency labels. Widget support added.
