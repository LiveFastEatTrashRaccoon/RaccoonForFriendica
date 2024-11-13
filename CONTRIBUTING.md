## How to build from source

This project uses the Gradle build tool. The recommended development environment is Android Studio
with the Kotlin Multiplatform Mobile plugin installed, which will automatically detect the build
tool and download a Gradle distribution for you.

Since the project is using Gradle 8.8 with the Android Gradle Plugin (AGP) version 8.5.2 please
make sure that you are using Android Studio Hedgehog or later -- have a
look [here](https://developer.android.com/build/releases/gradle-plugin?hl=en#android_gradle_plugin_and_android_studio_compatibility)
for a compatibility matrix between versions of Gradle, AGP and Android Studio
and [here](https://kotlinlang.org/docs/multiplatform-compatibility-guide.html#version-compatibility)
for more detailed information about KMP.

In order for Gradle to build, you will need a JDK installed on your local development machine, if
you are using stock Android Studio it ships with the JetBrains runtime. If you want to use your
custom JDK (e.g. under Linux you may want to try OpenJDK instead), please make sure that it has a
suitable version, according
to [this page](https://docs.gradle.org/current/userguide/compatibility.html).

Finally, since building this project requires quite a lot of RAM, please make sure that
the `gradle.properties` file in the root folder contains proper memory settings for the JVM and the
Kotlin compile daemon:

```properties
org.gradle.jvmargs=-Xmx4096M -Dfile.encoding=UTF-8 -Dkotlin.daemon.jvm.options\="-Xmx4096M"
```

Moreover, this app uses Sentry for crash reporting, but its configuration is not committed in the
VCS for safety reasons. In order to be able to build the project, please create an
empty `build.properties` file in the `core/utils` directory with the following content:

```properties
sentry_dsn=dummy_dsn
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

- [ ] checkout the `master` branch
- [ ] increment `versionCode` (ε) and `versionName` (α.β.γ-betaδ) in `composeApp/build.gradle.kts`
- [ ] add everything to stage and create a commit with the message "version α.β.γ-betaδ"
- [ ] tag the commit with the label "α.β.γ-betaδ"
- [ ] push both the commit and tag to `origin`
- [ ] (optional) create an announcement in the Friendica group

#### Stable releases

- [ ] checkout the `master` branch
- [ ] increment `versionCode` (ε) and `versionName` (α.β.γ) in `composeApp/build.gradle.kts`
- [ ] create a file called `ε.txt` under `fastlane/metadata/android/en-US/changelogs/` with the
  change list
- [ ] _copy_ the changelog file to `res/changelog.txt`
- [ ] add everything to stage and create a commit with the message "version α.β.γ"
- [ ] tag the commit with the label "α.β.γ"
- [ ] push both the commit and tag to `origin`
- [ ] (optional) create an announcement in the Friendica group
