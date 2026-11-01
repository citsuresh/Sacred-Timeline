---
name: astrological-timeline-engine
description: Provides architectural guidelines and implementation patterns for building high-precision ephemeris-driven astrological calculation engines (Drik Siddhanta, Lagna/Nakshatra computation, Lahiri Ayanamsha), multi-track 24-hour staggered vertical timeline UI components (TimingCard, Canvas path drawing, sub-horai breakdowns), and multi-factor weighted scoring engines (0-100% personal power meters with bilingual localization).
---

# Astrological Timeline & Ephemeris Engine Skill

Use this skill when developing, refactoring, or extending applications that combine high-precision astronomical ephemeris calculations (Meeus/ELP-2000, sidereal ascendants) with complex 24-hour vertical timeline UI components, custom canvas track layouts, and personalized multi-factor scoring engines.

## 1. Core Architecture & Data Flow
- **Domain Logic (`logic/`, `model/`)**: Ephemeris-driven calculations (Lagna, Nakshatra, Tithi, Sub-Horai) decoupled from UI via SSOT providers (`DayDataProvider`).
- **Presentation (`ui/dashboard/`)**: State-driven Compose UI utilizing custom Canvas drawing for stepped sticker-look timing cards and dynamic sub-horai column rendering.
- **Infrastructure (`data/`, `worker/`)**: DataStore configuration persistence, atomic disk JSON caches (`CacheManager`), and WorkManager background transition schedulers (`WidgetUpdateWorker`, `ReminderWorker`) with WakeLocks.

## 2. Ephemeris & Astrological Calculations
- **Drik Siddhanta (Thirukanitha)**: Prioritize ephemeris-derived sidereal math over legacy Vakya formulas to ensure sub-minute accuracy synchronized with the actual sky.
- **Proportional Timings**: Calculate proportional slot divisions (e.g., Rahu Kalam, Sub-Horas) anchored to local computed Sunrise and Sunset.
- **Natal Cross-Referencing**: Map birth parameters (Lagna, Rasi, Nakshatra) to evaluate Trikona/Dustana lordships, Thara Balam, and Chandrashtamam transits.

## 3. UI & Canvas Layout Guidelines
- **Sticker-Look Borders**: Use dual-border canvas path rendering (white outer stroke, semi-transparent black inner stroke) for legibility across any theme or wallpaper.
- **Adaptive Text & Truncation**: Adapt text rendering based on available column width (`widestSegment.widthFactor`) to prevent ugly ellipsis truncation (`...`) in narrow multi-column Universal views while rendering full details in wide single-column views.
- **Detail Panel Transparency**: Ensure every card opens a scrollable full-height modal bottom sheet (`DashboardDetailSheet`) providing complete mathematical and astrological justification breakdowns.

## 4. Multi-Factor Scoring Engine
- Synthesize active timeline layers (Horas, Gowri, Rahu Kalam, Muhurthams, Chandrashtamam, Thara Balam, Yogam synergy) using decoupled configuration weights (`ScoringWeightsConfig`).
- Display scores via real-time color-changing dial gauges (`PersonalPowerMeter`) with full point-by-point justification lists in detail panels and bilingual localization parity (English & Tamil).
