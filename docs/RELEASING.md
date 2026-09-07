# Maven Central releases

GitHub Packages receives every release tag through `publish-gh.yml`. Maven
Central receives selected versions through the manual **Upload Maven Central
Release** workflow, `publish-central.yml`. Both use the same release version;
there is no separate version sequence for Central.

Choose `fix` when a correction needs to reach consumers of the library now.
Otherwise, batch useful changes into at most one `monthly` feature release per
calendar month, skipping months with nothing worth publishing. These are
maintainer decisions: the reason labels the workflow run, and CI does not infer
urgency from commit messages or enforce a monthly quota. All automated version
bumps here are patch bumps, including features and breaking changes.

The 44-release monthly exemption is shared across Sava packages. Check the
remaining organization-wide allowance before releasing; this repository does
not track it. One workflow uploads both `idl-clients-spl` and
`idl-clients-bundle` together because bundle depends on the same SPL version.
One upload should not be assumed to count as one release against that allowance.

## Run a release

1. Select an existing, published GitHub release tag and review its changes since
   the last Central release. Complete the certification and fuzzing release
   checklist in `AGENTS.md`, and confirm that the dependencies in its published
   POMs are also available on Central. A build resolving dependencies from
   GitHub Packages does not establish that Central consumers can resolve them.
2. In **Actions → Upload Maven Central Release → Run workflow**, use `main`
   for the workflow branch, enter the release tag, and select `fix` or `monthly`.
   The workflow checks out the tag, verifies its release manifest, runs `check`,
   and signs and uploads the two modules to Central. It does not upload to
   GitHub Packages again.
3. Review the validated deployment in the
   [Central Portal](https://central.sonatype.com/publishing/deployments) and
   publish it. The Gradle task uses `USER_MANAGED`: a successful workflow means
   an uploaded, validated deployment, not a publicly available release.

To upload GitHub's latest published release, run the helper with the required
release reason:

```shell
./publish-central.sh fix
# Or, for the monthly feature release:
./publish-central.sh monthly
```

It requires an authenticated GitHub CLI (`gh auth login`), looks up the latest
release remotely, prints the selected tag, and dispatches the workflow on `main`.
It always targets `sava-software/idl-clients`, regardless of the current directory.

To select a specific release instead:

```shell
gh workflow run publish-central.yml --ref main -f tag=25.19.6 -f reason=monthly
```

The workflow must first exist on the default branch to be manually dispatched.
The tag input selects the source and artifact version independently of the
workflow branch. Only numeric release tags with a matching release manifest
are accepted; drafts and prereleases are rejected.

Do not rerun an upload after success just to publish the staged deployment:
use the portal. After a failed or interrupted upload, check the portal before
retrying because it may have accepted the deployment. A version already
published to Central cannot be overwritten.

## CI configuration

Reuse the publishing secrets already used by Sava:

- `READ_SAVA_PACKAGES` for build dependencies.
- `GPG_PUBLISH_SECRET` and `GPG_PUBLISH_PHRASE` for artifact signatures.
- `MAVEN_CENTRAL_TOKEN` and `MAVEN_CENTRAL_SECRET` for the Central Portal.

The JDK setup uses the existing `JDK_SRC` and `GRADLE_JAVA_VERSION` repository
variables. The release tag's first component selects the library's Java version,
as it does in the GitHub Packages workflow. The Central workflow has a separate
concurrency group and never cancels an upload already running.
