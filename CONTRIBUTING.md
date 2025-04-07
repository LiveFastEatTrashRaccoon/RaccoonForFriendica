## How to build from source

This project uses the Gradle build tool. The recommended development environment is Android Studio
with the Kotlin Multiplatform plugin installed, which will automatically detect the project and
download the necessary dependencies for you.

Since the project is using Gradle 8.10.2 with the Android Gradle Plugin (AGP) version 8.7.2 you
should use Android Studio Ladybug or later (have a
look [here](https://developer.android.com/build/releases/gradle-plugin?hl=en#android_gradle_plugin_and_android_studio_compatibility)
for a compatibility matrix between versions of Gradle, AGP and Android Studio)
and [here](https://kotlinlang.org/docs/multiplatform-compatibility-guide.html) for the
compatibility between the Kotlin Multiplatform plugin, Kotlin, Gradle and AGP.
Alternatively, you can try and use IntelliJ IDEA or Fleet but some extra steps may be needed to
ensure everything fits and runs together.

In order for Gradle to build, you will need to have a JDK installed on your local development
machine, if you are using stock Android Studio it ships with the default JetBrains runtime, you
could have a look in the Settings dialog under the section "Build, Execution, Deployment > Build
Tools > Gradle"in the "Gradle JDK" location drop-down menu.

If you want to use your custom JDK (e.g. under Linux you want to try OpenJDK instead), please make
sure that it has a suitable version, according
to [this page](https://docs.gradle.org/current/userguide/compatibility.html).

Since building this project requires a quite lot of RAM due to its multi-module structure
and to the fact that it is quite a complex project, please make sure that the `gradle.properties`
file in the root folder contains proper memory settings for the JVM and the Kotlin compile daemon:

```properties
org.gradle.jvmargs=-Xmx4096M -Dfile.encoding=UTF-8 -Dkotlin.daemon.jvm.options\="-Xmx4096M"
```

Finally, this app uses Sentry for crash reporting, but its configuration is not committed in the
VCS for safety reasons. In order to be able to build the project, please create an
empty `build.properties` file in the `core/utils` directory with the following content:

```properties
sentry_dsn=dummy_dsn
```

Similarly, create a `core/utils/build.properties` with the dummy configuration for the fallback
translation API:
```properties
inner_translation_api_url=dummy_url
```

## Release checklists

Symbols used in version numbers:

| Number | Meaning             |
|--------|---------------------|
| α      | major version       |
| β      | minor version       |
| γ      | patch version       |
| δ      | pre-release version |
| ε      | build number        |

#### Beta releases

In order to create version `α.β.γ-betaδ`

- [ ] checkout the `master` branch
- [ ] increment `versionCode` (ε) and `versionName` (α.β.γ-betaδ) in `composeApp/build.gradle.kts`
- [ ] create a file called `ε.txt` under `fastlane/metadata/android/en-US/changelogs/` with the
  changes you want to display on the stores (remember: 500 character limit)
- [ ] add everything to stage and create a commit with the message "version α.β.γ-betaδ"
- [ ] tag the commit with the label "α.β.γ-betaδ"
- [ ] push both the commit and tag to `origin` and wait for the workflows to finish
- [ ] create a signed AAB and upload it to Google Play in the `beta` track (open tests) with a
  summary of the changelog
- [ ] (optional) create an announcement in the Friendica group

#### Stable releases

In order to create version `α.β.γ`

- [ ] checkout the `master` branch
- [ ] increment `versionCode` (ε) and `versionName` (α.β.γ-betaδ) in `composeApp/build.gradle.kts`
- [ ] update `distribution/changelog.txt` with a detailed change list, remembering:
  - to include PR (with author) and issue references (if possible)
  - to update the version comparison for GitHub diff view
- [ ] create a file called `ε.txt` under `fastlane/metadata/android/en-US/changelogs/` with the
  changes you want to display on the stores (remember: 500 character limit)
- [ ] add everything to stage and create a commit with the message "version α.β.γ"
- [ ] tag the commit with the label "α.β.γ"
- [ ] push both the commit and tag to `origin` and wait for the workflows to finish
- [ ] create a signed AAB and upload it to Google Play in the `production` track using the content
  of `ε.txt` as the changelog
- [ ] (optional) create an announcement in the Friendica group
