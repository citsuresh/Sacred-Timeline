# Implementation Plan - Personalized Astrological Profile, Localized Static/Dynamic Guidance Strings, Docs Commit & Risk-Sorted Phased Breakdown

## User Review Required

> [!IMPORTANT]
> This refined implementation plan incorporates your guidance on localization:
> 1. **Localized Guidance Text (Static Resources for Dynamic Houses)**:
     - All transit impact guidance text (both general significance and house-specific transit interpretations for houses 1 through 12) will be **fully externalized into static string resources (`R.string...`)** in both English (`values/strings.xml`) and Tamil (`values-ta/strings.xml`). 
     - At runtime, the app dynamically selects the appropriate localized string resource based on the computed house number, ensuring 100% strict localization parity with zero hardcoded English text.
2. **Implementation Plan in Docs Folder & Committed**:
   - Per your request, the implementation plan is being copied to `docs/implementation_plan.md` and committed to git for easy review.

## Phased Breakdown by Sequence & Priority

### Phase 1: Dual Name Fields, Geocoder Auto-Suggest, Dedicated Profile Page (with Peyarchi Header Events & Localized Guidance Strings, Dasa/Bhukti, Transits & Charts), Default English & South Indian Style & Settings (Priority 1 - Foundation)
- **Files**: `BirthProfile.kt`, `SettingsRepository.kt`, `SettingsScreen.kt`, `ProfileAddDialog.kt`, `ProfileScreen.kt`, `AstroChartCanvas.kt`, `PortraitTimelineLayout.kt`, `DashboardDetailSheet.kt` (supporting Peyarchi detail cards with localized static guidance string resources for houses 1-12), `strings.xml`, `strings.xml (Tamil)`
- **Scope**: Add profile icon to top app bar actions. Build dedicated **User Profile Page** (`ProfileScreen.kt`) featuring responsive portrait/landscape layout, visual Rasi (D1) and Navamsa (D9) charts with South/North style selectors per profile, Graha Sthithi table, **Macro Insights** (active Dasa/Bhukti, Jupiter/Saturn transit houses), and full bilingual Tamil/English localization. Add **"Add Profile"** dialog with dual name fields (English + Regional) and Geocoder auto-suggest location search. Add **Planetary Peyarchi / Ingress detection** to automatically display transit transition events in the timeline header on the exact date of ingress, with interactive detail sheets showing whether the transit is Beneficial, Challenging, or Neutral relative to natal Rasi, using localized static string resources for house guidance. Default first start to English language and South Indian chart style. Implement robust graceful fallbacks when birth details are absent.
- **Dependencies**: None.
- **Mandatory Review**: Spawn Intermediate Regression Auditor sub-agent upon completion of Phase 1.

### Phase 2: User-Specific Natal Hora Compatibility & Detail Sheet (Priority 2)
- **Files**: `DashboardDetailSheet.kt`, `TimingCard.kt`, `PanchangamWidget.kt`
- **Scope**: Evaluate natal Trikona / Dustana lordships for Horas. Display Person Icon indicator (Green/Red/White) on timeline cards and detail sheets when a birth profile exists.
- **Dependencies**: Depends on Phase 1.
- **Mandatory Review**: Spawn Intermediate Regression Auditor sub-agent upon completion of Phase 2.

### Phase 3: Maitra Muhurtham & Thara Balam Synergies (Priority 3)
- **Files**: `DashboardDetailSheet.kt`, `TimingCard.kt`, `Metadata.kt`
- **Scope**: Cross-reference Maitra Muhurtham and daily Yogas with user's Thara Balam.
- **Dependencies**: Depends on Phase 1 & 2.
- **Mandatory Review**: Spawn Intermediate Regression Auditor sub-agent upon completion of Part 3.

### Phase 4: Scoring Weights Config, Multi-Factor Scoring Engine & Power Meter (Priority 4 - Advanced)
- **Files**: `ScoringWeightsConfig.kt`, `SettingsScreen.kt`, `MultiFactorScoringEngine.kt`, `PersonalPowerMeter.kt`, `DashboardDetailSheet.kt`
- **Scope**: Build `ScoringWeightsConfig` and Settings weights customization sub-screen. Implement `MultiFactorScoringEngine` (0-100% tactical score incorporating Paksha, Rikta tithis, Amavasya, Pradamai, and natal factors). Render color-changing dial gauge above timeline and on widget.
- **Dependencies**: Depends on Phase 1, 2, & 3.
- **Mandatory Review**: Spawn Intermediate Regression Auditor sub-agent upon completion of Phase 4.

## Verification & Audit Plan

### Automated Tests
- Build & assemble debug APK: `gradle_build(":app:assembleDebug")`.
- Run unit tests: `gradle_build(":app:testDebugUnitTest")`.

### Mandatory Regression Audits (Protocol 1 & Standing Rules)
- **Intermediate Audits**: Spawning the Regression Auditor sub-agent after *each* completed phase (Phases 1, 2, 3, and 4).
- **Final Comprehensive Audit**: Spawning the final Regression Auditor sub-agent after all phases are complete to verify test pass rates, strict localization parity (English & Tamil strings), and README documentation parity.
