---
name: tamil-panchangam-ephemeris-engine
description: Expert guidance and architectural patterns for developing high-precision Tamil Panchangam and Vedic ephemeris engines in Android apps. Covers astronomical solar/lunar calculations (ELP-2000, Meeus, Lahiri Ayanamsha), geodetic sunrise/sunset computation, proportional 8-part Rahu/Yama/Gulika scheduling, Hora planetary hour calculations, cross-midnight interval handling, and the authentic 95-part proportional Sub-Horai (Sukshma Horai) weighting system. Use this skill when implementing traditional almanac timings, ephemeris-based Drik Siddhanta calculations, or custom 24-hour astrological timeline visualizers.
---

# Tamil Panchangam Ephemeris & Muhurta Engine

## Overview
This skill provides technical guidance and structural patterns for building professional, high-precision Hindu/Tamil Panchangam applications that combine rigorous modern ephemeris astronomy (Drik Siddhanta) with traditional astrological almanac conventions (Nalla Neram, Horas, Sub-Horai, Rahu Kalam, etc.).

## Core Architecture

### 1. Ephemeris Astronomy Layer
- **Solar & Lunar Position**: Implements ELP-2000 (Meeus) lunar ephemeris and Lahiri Ayanamsha for precise Nirayana (sidereal) zodiac calculations.
- **Geodetic Sunrise & Sunset**: Computes exact local sunrise and sunset times based on latitude, longitude, date, and user-selectable horizon definitions (Scientific Top-Edge vs. Traditional Center-of-Disk).

### 2. Traditional Timing Calculations
- **Rahu Kalam, Yamagandam, Kuligai**: Divides daylight into 8 equal proportional segments and applies traditional weekday sequence mapping tables.
- **Hora & Sub-Horai (Sukshma Horai)**:
  - Divides daylight into 12 true ephemeris Horas anchored by the weekday (Vara) lord at sunrise.
  - **95-Part Proportional Sub-Horai**: Subdivides each main Hora into 7 unequal micro-segments using the authentic Tamil weighting system summing to 95 parts:
    - Sun: 6 | Venus: 20 | Mercury: 17 | Moon: 10 | Saturn: 19 | Jupiter: 16 | Mars: 7
    - Formula: $\text{Duration} = D_{\text{hora}} \times (\text{Parts} / 95)$
- **Cross-Midnight Handling**: Robustly handles intervals that cross midnight (`endTime < startTime`) by adjusting duration calculations across date boundaries.

### 3. UI Visualization & Performance
- **Dynamic Active Box Tracking**: Renders resource-intensive micro-strips (like Sub-Horai 25% width columns) exclusively for the single active Hora where the "NOW" line sits, preventing main-thread jank and frame drops.
- **Proportional Stacked Modal Detail Sheets**: Renders detailed breakdowns as continuously stacked proportional colored boxes without borders, complete with a live real-time "NOW" progress line.
