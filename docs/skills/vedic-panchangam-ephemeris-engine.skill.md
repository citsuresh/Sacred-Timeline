---
name: vedic-panchangam-ephemeris-engine
description: Technical architectural guidance and mathematical implementation protocols for developing high-precision Vedic Panchangam and astrological ephemeris applications in Kotlin, covering ELP-2000 Meeus lunar/solar math, Lahiri Ayanamsha, Udaya Tithi (sunrise-anchored) calculation, Vimsottari Dasa engines, and multi-factor astrological scoring systems.
---

# Skill: Vedic Panchangam & Ephemeris Engine Architecture

## Overview
High-precision astrological applications require sub-minute accuracy matching professional ephemerides (Drik Siddhanta). This skill defines architectural standards for astronomical calculations and scoring engines in Kotlin.

## Key Architectural Domains

### 1. Astronomical Ephemeris Math (Drik Siddhanta)
- **Sidereal Calculations**: Utilize ELP-2000 / Meeus high-precision lunar and solar formulas combined with Lahiri Ayanamsha subtraction to determine accurate Nirayana positions.
- **Udaya Tithi (Sunrise Anchoring / Udaya Vyapini)**: Determine the calendar day's primary Tithi and lunar metadata by evaluating instantaneous lunar values at **local sunrise** rather than midday, preventing skipped short tithis.

### 2. Astrological Scoring Engine (`ScoringEngine.kt`)
- **Multi-Factor Weighting**: Calculate weighted potency scores (0–100) combining Thara Bala, Gochara transits, Dasa/Bhukti periods, Hora compatibility, Yoga strength, and Tithi quality.
- **Time-Sensitive Evaluation**: Evaluate scores dynamically against specific time instants (`LocalTime`) rather than static daily averages, supporting real-time NOW line gauges and interactive inspector preview scrubbing.
- **Malefic Safety Overrides**: Apply severe penalty multipliers or caps during malefic windows (Rahu Kalam, Yamagandam, and Chandrashtamam).

### 3. Localization Parity
- **Bilingual English & Tamil**: Maintain 1:1 string resource parity across English (`values/strings.xml`) and regional scripts (`values-ta/strings.xml`) for all traditional technical terms, ratings, and metadata.
