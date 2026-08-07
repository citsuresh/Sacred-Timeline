# Agent Standing Rules

This document defines mandatory protocols for AI agents working on the Sacred Timeline project.

## 1. Regression Audit Protocol
Before considering any code change complete: after each meaningful part of a multi-part change, spawn a sub-agent via the task tool with this brief: 

> "Act as a Regression Auditor. Review this change specifically for: 
> (1) any existing behavior, caller, or shared state this diff could have altered without being asked to — check imports, method signatures, callers of anything touched, and any config/state other code relies on; 
> (2) whether the change contradicts or duplicates anything already established elsewhere in the codebase; 
> (3) if a build/test can be run, run it and report actual results, not just a read-through;
> (4) **'Wiring' Integrity**: Pay extra attention to how state and parameters are passed through nested calls. Verify that no 'silent defaults' are ignoring user preferences, and ensure that cache-clearing or job-cancellation triggers are correctly synchronized across all ViewModels/Repositories.
>
> If you find an issue outside the scope of the current change, report it only — do not investigate further, do not touch it, do not suggest fixing it now, even if it looks trivial; note it as a candidate for a known-issues list instead. 
> For issues within scope, propose a fix as a suggested diff only — never apply it yourself." 

After the full change is complete, spawn one more sub-agent with the same brief against the complete diff. 

**Reporting Rules:**
- Always surface the full Review Report to the user before proceeding.
- Never summarize the report away; provide the detailed findings.
- Never apply any proposed fix without explicit user approval.
- Never act on out-of-scope findings.
