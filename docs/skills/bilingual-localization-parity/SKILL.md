---
name: bilingual-localization-parity
description: Provides guidelines for enforcing zero hardcoded strings and strict bilingual translation parity (English and Tamil) in Android applications, covering string externalization, dynamic formatting arguments, and verification against lint inspections.
---

# Bilingual Localization Parity (English & Tamil)

## Overview
Ensuring that every user-facing string, card description, metadata, title, button, guidance, and activity list uses `stringResource` and has complete, non-fallback translations in both English (`values/strings.xml`) and regional languages like Tamil (`values-ta/strings.xml`).

## Key Principles
1. **Zero Hardcoded Strings**: NEVER hardcode user-facing string literals in Kotlin or Compose code. Always use `stringResource(R.string...)` or dynamic string formatters (`context.getString(R.string..., arg)`).
2. **Strict Bilingual Synchronization**: Every new string resource added to English must immediately receive its fluent Tamil translation in `values-ta/strings.xml`.
3. **Lint & Inspection Compliance**: Leverage Android Studio lint checks to catch unlocalized strings or missing translation keys across resource files before building.
