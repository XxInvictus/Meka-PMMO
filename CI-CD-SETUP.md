# CI/CD Setup Guide

This project uses GitHub Actions for automated building, testing, and publishing.

## Current Direction

This repository uses “new-*” workflows which delegate most CI/CD logic to
reusable workflows in the `XxInvictus/mc_universal_workflow` repository.

These workflows live in `.github/workflows/`:

- `1-build-test.yml` (Build & Test)
- `2-build-release.yml` (Build & Release)

## Upstream workflow contract (strict)

The reusable workflows enforce a strict, fail-fast contract. Before changing
build logic, read the upstream docs:

- [Contract overview (required keys, repo layout rules)](https://github.com/XxInvictus/mc_universal_workflow#contract-non-negotiable)
- [Required gradle.properties (canonical keys)](https://github.com/XxInvictus/mc_universal_workflow#required-gradleproperties)
- [Artifact naming contract (enforced paths)](https://github.com/XxInvictus/mc_universal_workflow/blob/main/build_docs/ARTIFACT_NAMING_CONTRACT.md)
- [Consumer integration guide (includes artifact handoff)](https://github.com/XxInvictus/mc_universal_workflow/blob/main/docs/INTEGRATION.md)

### Required `gradle.properties` keys (minimum)

At minimum, the upstream workflows validate that these canonical keys exist and are sane:

```properties
minecraft_version=...
mod_id=...
mod_version=...
java_version=...
loader_multi=false
loader_type=neoforge
```

If you migrate to multi-loader, the contract changes to `loader_multi=true` and requires `active_loaders=...`.

### Enforced artifact path (minimum)

For single-loader repos, artifact naming is derived from `gradle.properties` and must match exactly:

```text
build/libs/${mod_id}-${loader_type}-${minecraft_version}-${mod_version}.jar
```

If you change jar naming (archivesBaseName, classifier, shadow, etc.), update it so the produced jar still matches the contract.

## Quick Setup (for releases)

### 1) Configure repository variables (project IDs)

Go to `Settings > Secrets and variables > Actions > Variables` and add:

| Variable Name | Description | Required For |
| :--- | :--- | :--- |
| `MODRINTH_PROJECT_ID` | Your Modrinth project ID | Publishing to Modrinth |
| `CURSEFORGE_PROJECT_ID` | Your CurseForge project ID (numeric) | Publishing to CurseForge |

### 2) Configure repository secrets (API tokens)

Go to `Settings > Secrets and variables > Actions > Secrets` and add:

| Secret Name | Description | Required For |
| :--- | :--- | :--- |
| `MODRINTH_TOKEN` | Modrinth API token | Publishing to Modrinth |
| `CURSEFORGE_TOKEN` | CurseForge API token | Publishing to CurseForge |

### 3) Get your tokens

#### Modrinth

- Token: <https://modrinth.com/settings/pats>
- Project ID: from your project URL: `https://modrinth.com/mod/{PROJECT_ID}`

#### CurseForge

- Token: <https://console.curseforge.com/#/api-keys>
- Project ID: numeric ID from your project page URL

## Branch workflow

```txt
main
 └─> <mc-version>-dev (development & testing)
      └─> <mc-version>-release (releases & publishing)
```

Examples:

- `1.20.1-dev` → `1.20.1-release`
- `1.21.1-dev` → `1.21.1-release`

### Development flow

1. Merge changes into `*-dev` to run build/test.
2. When ready to publish, merge into `*-release`.

### Release notes

- `2-build-release.yml` uses a reusable release workflow and inherits secrets from this repo.
- To skip publishing for a particular release-branch push, include `[SKIP-RELEASE]` in the commit message.
