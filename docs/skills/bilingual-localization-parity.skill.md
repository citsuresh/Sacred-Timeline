---
name: bilingual-localization-parity
description: Provides architectural guidelines and validation protocols for achieving 100% string externalization and strict parallel bilingual parity (e.g. English and Tamil) across complex domain metadata, card descriptions, guidance messages, strategic activities, and UI dialog components in Android applications.
---

# Bilingual Localization Parity

## Overview
In multi-language Android apps (such as English and Tamil), hardcoded strings, missing resource keys, or mismatched array references lead to untranslated UI elements or incorrect fallback text. This skill defines the mandatory protocol for achieving strict bilingual localization parity.

## Core Guidelines

### 1. Zero Hardcoded Strings
- Never hardcode user-facing strings, button labels, dialog prompts, or descriptions in Kotlin/Compose code.
- Always use `stringResource(R.string...)` or string arrays.

### 2. Strict Bilingual Parity
- Every new string resource added to English (`values/strings.xml`) must have an accurate, fluent translation added to Tamil (`values-ta/strings.xml`).
- Pay special attention to domain metadata, astronomical descriptions (`desc_star_1` to `desc_star_27`, `tithi_desc_...`), guidance summaries, and activity lists.

### 3. Regression Audit for Localization
- The Regression Auditor must verify that all UI cards, detail sheets, and settings screens display correct, localized text in both languages with zero English fallbacks in non-English modes.
