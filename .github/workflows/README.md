# GitHub Actions Workflows

This directory contains the CI/CD workflows for the Meka-PMMO project.

## Workflows

### 1. Version Check (`version-check.yml`)

**Triggers:**
- Pull Request to `1.20.1-release` branch

**Jobs:**
1. **Check Version Updated** - Verifies that `mod_version` in `gradle.properties` has been incremented

**Features:**
- Compares current PR version against base branch version
- Blocks PR merge if version hasn't been updated
- Can be overridden with force merge (requires admin privileges)
- Provides clear error messages and step summaries

**Purpose:** Prevents accidentally releasing without updating the version number.

### 2. Dev Build & Test (`dev-build.yml`)

**Triggers:** 
- Push or Pull Request to `1.20.1-dev` branch
- Manual workflow dispatch

**Manual Run Options:**
- `run_tests` - Choose whether to run tests after build (true/false)
- `test_type` - Select which tests to run (both/client/server/none)

**Jobs:**
1. **Build** - Builds the mod and extracts metadata from `gradle.properties`
2. **Test Client** - Downloads dependencies and tests the mod in a client environment
3. **Test Server** - Downloads dependencies and tests the mod in a server environment
4. **Cleanup** - Removes temporary artifacts after tests complete

**Features:**
- Automatic dependency download (Mekanism via modmaven.dev, PMMO via Modrinth API)
- Runtime testing using `headlesshq/mc-runtime-test@4.1.0`
- Build artifacts retention for 3 days
- Dependency artifacts retention for 1 day
- Path filters to skip unnecessary builds
- Concurrency controls to cancel in-progress runs
- Draft PR detection to skip testing
- Automatic artifact cleanup

### 2. Release Build & Publish (`release.yml`)

**Triggers:** 
- Push or Pull Request to `1.20.1-release` branch
- Manual workflow dispatch

**Manual Run Options:**
- `dry_run` - Build and tag only, skip all publishing (useful for testing)
- `publish_github` - Enable/disable publishing to GitHub Releases
- `publish_modrinth` - Enable/disable publishing to Modrinth
- `publish_curseforge` - Enable/disable publishing to CurseForge
- `skip_tag` - Skip tag creation (useful when tag already exists)
- `custom_changelog` - Override CHANGELOG.md with custom text

**Jobs:**
1. **Build & Tag** - Builds the mod, creates a git tag with version (idempotent)
2. **Release** - Creates or updates a GitHub release with changelog
3. **Publish** - Publishes to GitHub Releases, Modrinth, and CurseForge
4. **Cleanup** - Removes temporary artifacts after successful publish

**Features:**
- Automatic version tagging (`${minecraft_version}-${mod_version}`)
- Intelligent changelog extraction from `CHANGELOG.md` (includes skipped versions)
- Multi-platform publishing using `Kir-Antipov/mc-publish@v3.3`
- Build artifacts retention for 30 days
- Idempotent tag creation (won't fail if tag exists)
- Idempotent release creation (updates existing releases)
- Graceful handling of duplicate version publishing
- Path filters to skip unnecessary builds
- Automatic artifact cleanup after successful publish

## Required GitHub Secrets

To use these workflows, you need to configure the following secrets in your repository settings (`Settings > Secrets and variables > Actions`):

### For Modrinth Publishing
- `MODRINTH_PROJECT_ID` - Your Modrinth project ID (found in project settings URL)
- `MODRINTH_TOKEN` - Modrinth API token (create at https://modrinth.com/settings/pats)

### For CurseForge Publishing
- `CURSEFORGE_PROJECT_ID` - Your CurseForge project ID (numeric ID from project page)
- `CURSEFORGE_TOKEN` - CurseForge API token (create at https://console.curseforge.com/#/api-keys)

### Notes
- `GITHUB_TOKEN` is automatically provided by GitHub Actions, no configuration needed

## Workflow Setup

### Setting up secrets:

1. **Modrinth:**
   - Go to https://modrinth.com/settings/pats
   - Create a new Personal Access Token with "Write" permissions
   - Copy the token and add it as `MODRINTH_TOKEN` in GitHub
   - Find your project ID from your project URL: `https://modrinth.com/mod/{PROJECT_ID}`

2. **CurseForge:**
   - Go to https://console.curseforge.com/#/api-keys
   - Create a new API key
   - Copy the token and add it as `CURSEFORGE_TOKEN` in GitHub
   - Find your project ID from your project page (numeric ID in the URL)

## Branch Strategy

- **`1.20.1-dev`** - Development branch for ongoing work
  - Triggers build and testing only
  - No releases or publishing
  - Good for validating changes before release

- **`1.20.1-release`** - Release branch for stable versions
  - Triggers full release pipeline
  - Creates git tags
  - Publishes to all platforms
  - Only push/merge here when ready to release

## Version Management

The version is controlled by `mod_version` in `gradle.properties`. The full version tag follows the format:
```
${minecraft_version}-${mod_version}
```

Example: `1.20.1-1.2.0`

## Changelog Format

The workflows expect `CHANGELOG.md` to follow this format:

```markdown
## [1.2.0] - 2025-12-04

### Added
- New feature

### Changed
- Modified behavior

### Fixed
- Bug fix
```

The release workflow will automatically extract the section for the current version **and any unreleased versions** since the last git tag. This means if you skip versions (e.g., release 1.0.2, skip 1.1.0, then release 1.2.0), the changelog will include both 1.2.0 and 1.1.0 sections.

## Testing

The test jobs will:
1. Download your built mod
2. Download dependencies (Mekanism, PMMO) from their respective sources
3. Set up a Minecraft Forge environment
4. Launch either client or server
5. Wait for successful startup (or timeout after 5 minutes)

Test success indicators:
- **Client:** `Reached DONE state`
- **Server:** `For help, type "help"`

## Troubleshooting

**Build fails:**
- Check Java version (must be 17)
- Verify `gradle.properties` syntax
- Ensure `gradlew` has execute permissions

**Tests fail:**
- Check dependency URLs are correct
- Verify mod is compatible with the Minecraft/Forge versions
- Review test timeout (currently 300 seconds)

**Publishing fails:**
- Verify all required secrets are set
- Check API tokens are valid and have correct permissions
- Ensure project IDs are correct
- Review dependency syntax in workflow file

**Re-running releases:**
- Both workflows are idempotent and safe to re-run
- Tag creation will be skipped if tag already exists
- GitHub releases will be updated rather than duplicated
- Platform publishing will skip existing versions gracefully

## Cost Optimization

The workflows include several cost-saving features:

- **Path filters** - Only trigger on relevant file changes
- **Concurrency controls** - Cancel redundant runs automatically
- **Conditional testing** - Skip tests for draft PRs
- **Artifact caching** - Reuse dependency downloads
- **Short retention** - Dev artifacts kept for 3 days, dependencies for 1 day
- **Automatic cleanup** - Artifacts deleted after job completion

For detailed cost analysis, see `COST_OPTIMIZATION.md`.

## Manual Workflow Execution

Both workflows support manual triggering via the Actions tab on GitHub:

**Dev Build & Test:**
- Choose whether to run tests
- Select which tests to run (client/server/both/none)

**Release Build & Publish:**
- Dry run mode for testing without publishing
- Selective platform publishing (GitHub/Modrinth/CurseForge)
- Custom changelog override
- Skip tag creation for re-runs
