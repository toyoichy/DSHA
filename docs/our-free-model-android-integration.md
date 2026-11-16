# Our Free Model Android integration

Status: implementation decision and source pin. This document does not claim that the plugin is already bundled, installed, or tested on a device.

## Target

Integrate the upstream Our Free Model plugin into the DSHA Android Standard runtime on the existing Ubuntu ARM64 / Node 24 / DSH 0.2.2-rc.1 environment. Keep the existing DSHA lifecycle, native permission boundaries, plugin manager, and data/backup model. Do not add a second Node server or a second Android shell.

The Android application remains standalone after installation: users do not need Termux or Debian installed separately. Network access is required for cloud model requests and for first-time plugin acquisition if the plugin is not bundled in the APK.

## Pinned upstream source

- Repository: https://github.com/Ebony-Vinyl/dsh-our-free-model
- Release: https://github.com/Ebony-Vinyl/dsh-our-free-model/releases/tag/v2.8.5
- Source revision recorded by upstream provenance: `1aca0e55795887881f5e879df473d07ca0435fcd`
- Release artifact: `dsh-our-free-model-2.8.5.tgz`
- SHA-256: `680d514addabaa4774b4d8c6114063f9b2bee65244637ab695383a3887e50232`
- License: MIT. Preserve the upstream LICENSE and vendor/channel-pack NOTICE and license files.

**Important package-name security rule:** never install the unscoped npm package `dsh-our-free-model`. The upstream README explicitly says that package is an unrelated third-party repack with modified files. The upstream project's scoped npm name is `@ebony-vinyl/dsh-our-free-model`; alternatively use only the exact GitHub Release tarball above and verify its SHA-256 before installation.

## Compatibility gate

Upstream `package.json` declares these peer ranges:

- `@deepseek-ai/cordis ^4.0.2`
- `@deepseek-ai/dsh-commands ^0.1.2-rc.1 || ^0.1.7-rc.2 || ^0.2.0-rc.1`
- `@deepseek-ai/dsh-credentials ^0.1.2-rc.1 || ^0.1.7-rc.2 || ^0.2.0-rc.1`
- `@deepseek-ai/dsh-llm ^0.1.2-rc.1 || ^0.1.7-rc.2 || ^0.2.0-rc.1`
- `@deepseek-ai/schemastery ^3.18.4`

The DSHA locked runtime currently pins DSH packages at `0.2.2-rc.1` and Cordis at `4.1.0`, which fall within the declared ranges. This is a dependency-range check only; it is not proof of successful plugin startup or Android device compatibility. The plugin README's displayed compatibility badge is older, so the integration must still run the real host/plugin startup tests before being enabled by default.

## Integration requirements

1. Acquire the exact upstream Release artifact or scoped upstream package. Verify the SHA-256 before unpacking. Do not use the unscoped npm name.
2. Install through the DSH plugin/package manager contract for the active `web` profile. Do not manually copy only `index.js` and `client.js`: upstream states that the complete package includes adapter, source, locale, engine resources, channel pack, WASM, LICENSE and NOTICE files.
3. Set `distribution=managed` in the plugin configuration so the APK/runtime pack owns update timing and avoids competing self-update, announcement-feed, and hot-reload paths.
4. Preserve user credentials in the DSH credential store. Never put channel credentials in APK assets, plugin source, browser local storage, build logs, or Git.
5. Treat anonymous/free channels as conditional services: model availability, region, quotas, provider policy, and rate limits can change. No personal API key is needed only for channels that explicitly support anonymous/free use. This is not offline or unlimited inference.
6. Keep plugin acquisition retryable and non-fatal to the base DSHA launcher. If network acquisition or verification fails, DSHA must still boot and expose a clear retry/status entry.
7. Do not reset, overwrite, or migrate existing DSH profiles, sessions, credentials, attachments, or user plugins during installation or upgrade.

## UI direction

- Preserve the existing DSHA native launcher and its existing navigation unless a verified gap requires a change.
- Use the existing web workbench/plugin UI for model selection and channel management rather than creating a parallel chat backend.
- Keep the selected product direction: adaptive light/dark theme and sidebar navigation. Reuse upstream mobile UI behavior where possible.
- Provide a native status/installation entry that distinguishes: not installed, downloading, checksum failed, install failed, ready, and update available. Never display "ready" before a real plugin load/health check.

## Required validation before APK release

- Verify the release tarball digest and upstream LICENSE/NOTICE files.
- Verify all required peer dependencies against the locked DSHA runtime.
- Run the plugin's own package/host tests against the locked DSH runtime.
- Test install, restart, model discovery, streaming response, provider/channel error handling, and credential persistence in a clean profile.
- Test failure/retry with network unavailable and checksum mismatch; base DSHA must remain usable.
- Run DSHA source hygiene, managed-runtime descriptor, host tests, unit tests, lint, and APK packaging gates.
- Install the exact APK on Android 16 / arm64 and verify first-run extraction, plugin install, chat, and upgrade without data loss.

No APK is considered complete until the build artifact and its SHA-256 are available and the exact artifact has passed the stated software checks. Device acceptance must be reported separately.
## Current implementation status (2026-11-17)

The current branch adds a native shortcut in the Plugins market card. Tapping it opens the existing `PluginInstallActivity` with the exact GitHub Release URL, package name/version, and pinned SHA-256 above. The existing installer is expected to show its package preview and require the user to confirm installation.

This is an install entry point, **not a pre-bundled plugin**. The plugin is not automatically present on a fresh install. After confirmation, the existing plugin installation pipeline performs acquisition and verification; runtime compatibility and actual plugin health still require validation. Free/anonymous channel access remains dependent on provider availability, quota, region, and policy.

The source branch currently has not produced a verified APK, and no Android build, unit-test run, lint run, plugin startup test, or device acceptance result is claimed. Do not merge or publish as release-ready until those gates have evidence.
