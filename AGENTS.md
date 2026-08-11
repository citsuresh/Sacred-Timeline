# Agent Standing Rules

This document defines mandatory protocols for AI agents working on the Sacred Timeline project.

## 1. Regression Audit Protocol
Before considering any code change complete: after each meaningful part of a multi-part change, spawn a sub-agent via the task tool with this brief: 

> "Act as a Regression Auditor. Review this change specifically for: 
> (1) any existing behavior, caller, or shared state this diff could have altered without being asked to — check imports, method signatures, callers of anything touched, and any config/state other code relies on; 
> (2) whether the change contradicts or duplicates anything already established elsewhere in the codebase; 
> (3) if a build/test can be run, run it and report actual results, not just a read-through;
> (4) **'Wiring' Integrity**: Pay extra attention to how state and parameters are passed through nested calls. Verify that no 'silent defaults' are ignoring user preferences, and ensure that cache-clearing or job-cancellation triggers are correctly synchronized across all ViewModels/Repositories.
> (5) **Feature Parity Checklist**: For any new timing, slot, or column added to the Timeline, verify it is also implemented in:
>     - **PanchangamWidget.kt** (UI visibility)
>     - **WidgetUpdateWorker.kt** (Transition/Boundary scheduling)
>     - **DayDataProvider.kt** (Calculation inclusion)
>     - **SettingsRepository.kt** (User toggle/visibility persistence)
> (6) **Finding Classification**: For every finding, explicitly classify it as one of: (a) unrelated to the agreed breakdown — treat as out-of-scope per the existing rule, or (b) within the scope of an already-planned future part of the breakdown — name which part, and flag it as "expected to be addressed in Part N".
>
> **Reporting Rules**:
> - For every finding (including out-of-scope items), **investigate fully** to understand the root cause.
> - **Propose a specific fix** (as a code snippet or diff).
> - **Provide an effort estimate** (e.g., "5 mins, Low Risk").
> - **NO ACTION POLICY**: Do not touch the files or apply any fix yourself. Report the findings to the user and wait for explicit permission to apply any specific fix.
> - If an issue is out-of-scope, note it as a candidate for a 'known-issues' list." 

After the full change is complete, spawn one more sub-agent with the same brief against the complete diff. 

## 2. Pre-Build Decomposition Protocol
Before implementing any non-trivial multi-part change, propose a breakdown of the work into the smallest independently-reviewable pieces and present this plan to the user for confirmation before writing any code. Do not decide on pieces ad hoc as implementation proceeds — the breakdown must be agreed upfront. If a piece turns out to be too tightly coupled to review separately from another, say so and propose combining them, rather than silently reviewing them together without flagging it.

## 3. Independent Review Requirement
When spawning the Regression Auditor sub-agent (per Protocol 1), it must review the diff independently, without relying on or referencing any prior explanation of why the change is correct that may exist in context. Do not pass the builder's own rationale into the sub-agent's brief; the sub-agent forms its assessment from the code itself.

## 4. Recurrence Escalation
If the Regression Auditor reports the same underlying issue two or more times across attempted fixes for the same change, stop attempting further incremental fixes. Tell the user directly and recommend starting a fresh, focused context to address the issue specifically, rather than continuing to iterate in an increasingly crowded one.

## 5. Quality Bar Anchoring
When judging whether new code is "good" or "correct" beyond functional regressions, anchor the judgment to something concrete rather than general taste: prefer existing conventions already established elsewhere in this codebase (naming, structure, error-handling patterns), explicit written acceptance criteria for genuinely new functionality, or passing tests — in that order of preference. Avoid open-ended quality judgments with no concrete reference point.

## General Reporting & Approval Rules
- Always surface the full Review Report to the user before proceeding.
- Never summarize the report away; provide the detailed findings, proposed fixes, and effort estimates.
- Never apply any proposed fix without explicit user approval.
- Never act on out-of-scope findings.
- Protocol 2's proposed breakdown requires explicit user confirmation before implementation begins.
- Protocol 4's recommendation to start a fresh context requires the user's decision before doing so — do not start a new context unilaterally.
- The sub-agent's report must state only what was checked and what was found — never characterize the overall result with confidence language ("robust," "certified," "solid," "production-ready," etc.). Findings and verification steps are facts; whether the result is good enough is the user's judgment to make, not the auditor's or the builder's to declare.
