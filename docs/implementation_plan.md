# Implementation Plan - Dual Hora Compatibility Indicators (3-Phase Approach)

## Phase 1: UI Finalization (Dummy Compatibility Data)
- **Scope**: 
  - Update `TimingCard.kt` to display **dual compatibility icons** in the Hora card header:
    - **Old Icon**: Positioned near the **top-left** of the header (weekday compatibility).
    - **New Icon**: Positioned near the **top-right** of the header using the **Person icon** (`Icons.Default.Person`) tinted with its compatibility color (Green/Red/White).
  - Use dummy compatibility data to validate the UX presentation.
- **Files**: `TimingCard.kt`

## Phase 2: Actual Compatibility Logic & Calculator Support
- **Scope**: 
  - Implement actual model and calculator logic (Lagna house lordships: Trikona 1,5,9 vs Dustana 6,8,12) for the new personal compatibility indicator.
- **Files**: `PanchangamCalculator.kt`, `MockPanchangamProvider.kt`, `DayDataProvider.kt`

## Phase 3: Dashboard Detail Sheet UI Update & Verification
- **Scope**:
  - Implement dual compatibility summary cards in the main header of `DashboardDetailSheet.kt`.
  - Implement dual icons for sub-hora list items, ensuring proper layout spacing so the NOW line text never overlaps them.
  - Verification & testing (build, unit tests, and review).
- **Files**: `DashboardDetailSheet.kt`
