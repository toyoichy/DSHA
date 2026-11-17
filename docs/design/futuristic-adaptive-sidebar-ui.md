# DSHA — Futuristic Adaptive UI and Sidebar Navigation

Status: implementation brief  
Target branch: `feat/futuristic-adaptive-sidebar-ui`  
Scope: native Android presentation layer only. This document does not authorize changes to the DSH runtime, device-command policy, permissions, bridge protocol, signing identity, or user data.

## Product decisions

- Visual direction: futuristic AI workspace (option 2), restrained and readable rather than decorative.
- Theme: adaptive; follow Android light/dark mode and the existing DSHA palette tokens. Do not introduce hard-coded colors in Java or layouts.
- Navigation: sidebar as the primary navigation pattern. Preserve the existing four destinations and their IDs: Launch, Plugins, Settings, Terminal.
- Runtime: retain the existing DSHA / DSH engine and its current lifecycle. UI work must not restart or migrate a running environment.
- Distribution target: Android 11+ (API 30), arm64-v8a, existing Standard application identity. Do not change package name, signing, version, or release workflow as part of UI work.

## Existing architecture to preserve

- `MainActivity` owns navigation, entry gates, browser/task lifecycle, and the `BottomNavigationView` route source.
- `ShellRail` already provides a tablet sidebar and delegates selection back to `MainActivity`.
- `activity_main.xml` is the phone shell; `layout-sw600dp/activity_main.xml` is the tablet shell.
- Existing navigation IDs are `nav_launch`, `nav_plugins`, `nav_settings`, and `nav_terminal`.
- Existing theme infrastructure is palette-token based (`dsha_pal_*`, `shell_*`) with day/night resource variants. Reuse it.
- The `BottomNavigationView` remains in both layouts as a hidden route controller because navigation code and tests rely on it. It is not removed; phone and tablet users navigate through the sidebar.

## Intended experience

### Phone (compact width)
- Replace the floating bottom navigation as the visible primary navigation with a collapsible/overlay sidebar opened from the app bar.
- Sidebar contains the four existing destinations, selected state, accessible labels, and a clear close/back behavior.
- Main content remains full-width when the sidebar is closed.
- Keep a narrow, unobtrusive app-bar menu affordance with a 48dp touch target.
- Do not permanently reserve a wide navigation column on compact screens.

### Tablet / expanded width
- Keep a persistent left navigation rail/sidebar.
- Allow the content pane to use the remaining width.
- Preserve the current settings detail-pane behavior and existing responsive breakpoints unless tests demonstrate a safe improvement.

### Visual language
- Dark and light modes use the same semantic palette roles.
- Use subtle layered surfaces, clear hierarchy, restrained accent glow/line details, and consistent rounded cards.
- Prioritize legibility, contrast, touch targets, TalkBack descriptions, keyboard focus, and large-font behavior over visual effects.
- Avoid decorative animation that delays navigation or consumes battery.

## Non-goals and safety boundaries

- No changes to model provider behavior, API-key storage, model defaults, or claims that a free model is guaranteed.
- No changes to proot/proroot, Ubuntu assets, DSH runtime descriptors, package locks, plugins, backup/restore formats, shell allowlists, Root/Shizuku/ADB policy, LAN authentication, or bridge routes.
- No deletion or reset of user data.
- No direct commits to `main`; implementation stays on this feature branch and is proposed through a pull request.
- Do not produce or publish a production-signed APK. The official signing key is not available to this workflow.

## Implementation sequence

1. [x] Add a compact-width sidebar that reuses the existing navigation IDs and delegates every selection to `MainActivity`.
2. [x] Wire the phone app-bar menu and rely on Dialog back/outside dismissal without changing route semantics.
3. [x] Keep `ShellRail` for expanded layouts; use the same palette-driven selected-state drawable.
4. [ ] Add/update unit and instrumentation tests for destination selection, back handling, state restoration, accessibility labels, and compact/expanded layouts.
5. [ ] Run the repository's prescribed formatting, UI/i18n, unit-test, and build checks. Record environment-only failures explicitly.
6. [x] Review scope: changes are limited to the phone layout, `MainActivity` menu wiring, the new sidebar class, and this design document.

## Acceptance checklist

- [ ] All four existing destinations remain reachable.
- [ ] Sidebar selection uses the existing MainActivity navigation path; no duplicate navigation state is introduced.
- [ ] Back closes the compact sidebar before leaving the current page.
- [ ] Rotation, process recreation, theme changes, and language changes preserve correct navigation state.
- [ ] Compact and expanded layouts remain usable at 320dp width and large font scale.
- [ ] TalkBack labels and focus order are correct; touch targets are at least 48dp.
- [ ] Light and dark themes both pass contrast and visual checks.
- [ ] No runtime descriptor, DSH asset, device-policy, permission, bridge, signing, or user-data behavior changes.
- [ ] Relevant tests and build tasks pass, with limitations recorded honestly.
