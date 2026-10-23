# Sacred Timeline - Multi-Factor Scoring Engine & Personal Power Meter Specification

## 1. Overview
The Multi-Factor Scoring Engine aggregates multiple independent astrological signals (Tithi, Nakshatra, Yoga, Thara Balam, Hora, Chandrashtama, Dasa influences, and Transits) into a single, profile-aware favorability score from **0 to 100%**, accompanied by human-friendly rating bands and a transparent factor breakdown.

---

## 2. Core Components

### A. Rating Bands & Color Coding
To avoid false precision while retaining quantitative precision, the score maps to both a percentage and a categorical interpretation band:
- **81% – 100%**: `Very Favorable` (Color: Green / Bright Gold)
- **61% – 80%**: `Favorable` (Color: Light Green)
- **41% – 60%**: `Mixed` (Color: Yellow)
- **21% – 40%**: `Challenging` (Color: Orange)
- **0% – 20%**: `Avoid` (Color: Deep Red)

### B. Scoring Contributors & Weights (`ScoringWeightsConfig`)
The score is computed from weighted contributors stored in settings. Default weights (totaling 100%):
1. **Thara Balam (Personal Star Strength)**: 20%
2. **Transit Influences (Jupiter / Saturn)**: 20%
3. **Dasa / Bhukti Influence**: 20%
4. **Hora Quality & Natal Compatibility**: 15%
5. **Yoga Quality**: 15%
6. **Tithi & Paksha Quality**: 10%

*Note: Critical overrides apply, such as an active personal Chandrashtamam applying a significant penalty.*

### C. Score Breakdown Transparency
Tapping the Personal Power Meter opens an audit dialog/sheet displaying:
- Current Score & Interpretation Band
- Itemized point contributors (`+15 Hora`, `+20 Thara Balam`, `-5 Chandrashtama`, etc.)
- Tailored activity recommendations based on the score and active profile.

---

## 3. UI Placement
- **Dashboard**: Positioned prominently above the 24-hour timeline as the **Personal Power Meter** card.
- **Widgets**: Glanceable percentage and band summary on home screen widgets.
