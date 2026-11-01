---
name: astrological-profile-and-dasa-engine
description: Provides architectural patterns and implementation workflows for building multi-profile astrological applications, Sidereal Lagna/Rasi calculation engines, Astro chart renderers (South and North Indian styles), and Vimsottari Dasa & Bhukti calculation engines with dual-lane visual tracks and pinch-to-zoom support. Use this skill when implementing astrological calculations, birth profile separation (active vs. primary), chart drawing canvases, or planetary period timelines.
---

# Astrological Profile & Vimsottari Dasa Engine

## Overview
This skill outlines the architecture and best practices for implementing multi-profile birth data management, high-precision Sidereal ephemeris calculations, Lagna lordship breakdowns, and Vimsottari Dasa/Bhukti timeline engines in Android applications.

## Key Architectural Components

### 1. Multi-Profile Storage & Separation
- **Data Model (`BirthProfile.kt`)**: Stores birth date, time, timezone offset, latitude, longitude, dual names (English and Regional), avatar initials, and primary profile flags.
- **Active vs. Primary Separation**:
  - **Active Profile**: Drives UI dashboard insights (Thara Balam, Chandrashtamam, Hora compatibility, charts, macro insights).
  - **Primary Profile**: Drives background workers (`ReminderWorker`, `WidgetUpdateWorker`) and Glance widgets (`PanchangamWidget`) for consistent background operation.

### 2. High-Precision Ephemeris & Lagna Engine (`BirthChartCalculator`)
- Computes Sidereal Ascendant (Lagna) and Moon Rasi houses using GMST/LST formulas and Lahiri Ayanamsha adjustments.
- Evaluates planetary houses and Lagna lordship classifications (Lagnadipathi, Yogakaraka, Trikona, Dusthana, Kendra, Upachaya, Maraka, Badhaka).

### 3. Vimsottari Dasa & Bhukti Calculator (`DasaCalculator` & `DasaBhuktiDialog`)
- Calculates 120-year Mahadasa and Bhukti spans with birth Nakshatra balance slicing.
- Renders dual-lane Gantt chart visual tracks supporting portrait (vertical) and landscape (horizontal) layouts with pinch-to-zoom and precision "Go to Now" scrolling.
