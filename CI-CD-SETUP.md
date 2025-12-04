# CI/CD Setup Guide

This project uses GitHub Actions for automated building, testing, and publishing.

## Quick Setup

### 1. Configure Repository Secrets

Go to `Settings > Secrets and variables > Actions > New repository secret` and add:

| Secret Name | Description | Required For |
|-------------|-------------|--------------|
| `MODRINTH_PROJECT_ID` | Your Modrinth project ID | Publishing to Modrinth |
| `MODRINTH_TOKEN` | Modrinth API token | Publishing to Modrinth |
| `CURSEFORGE_PROJECT_ID` | Your CurseForge project ID | Publishing to CurseForge |
| `CURSEFORGE_TOKEN` | CurseForge API token | Publishing to CurseForge |

### 2. Get Your Tokens

**Modrinth:**
- Token: <https://modrinth.com/settings/pats>
- Project ID: From your project URL `https://modrinth.com/mod/{PROJECT_ID}`

**CurseForge:**
- Token: <https://console.curseforge.com/#/api-keys>
- Project ID: From your project page URL (numeric ID)

### 3. Branch Workflow

```
main
 └─> 1.20.1-dev (development & testing)
      └─> 1.20.1-release (releases & publishing)
```

**Development Flow:**
1. Make changes in feature branches
2. Merge to `1.20.1-dev` for automated testing
3. When ready to release, merge to `1.20.1-release`

**Version Updates:**
1. Update `mod_version` in `gradle.properties`
2. Add changelog entry in `CHANGELOG.md`
3. Merge to `1.20.1-release`
4. Workflow automatically tags, releases, and publishes

## Workflows

### Version Check (PRs to `1.20.1-release`)
- ✅ Verify `mod_version` has been updated
- ✅ Block merge if version unchanged
- ℹ️ Can be overridden with force merge (admin only)

### Dev Build (`1.20.1-dev` branch)
- ✅ Build mod
- ✅ Test client startup
- ✅ Test server startup
- ✅ Manual run with flexible test options
- ❌ No releases or publishing

### Release (`1.20.1-release` branch)
- ✅ Build mod
- ✅ Create git tag (idempotent)
- ✅ Create GitHub release (idempotent)
- ✅ Publish to Modrinth
- ✅ Publish to CurseForge
- ✅ Manual run with dry-run and selective publishing
- ✅ Custom changelog override option
- ✅ Includes skipped version changelogs

## More Information

See [.github/workflows/README.md](.github/workflows/README.md) for detailed documentation.
