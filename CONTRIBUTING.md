## How to build from source

This project uses the Gradle build tool. The recommended development environment is Android Studio
with the Kotlin Multiplatform Mobile plugin installed, which will automatically detect the build
tool and download a Gradle distribution for you.

Since the project is using Gradle 8.7 with the Android Gradle Plugin (AGP) version 8.2.2 please
make sure that you are using Android Studio Hedgehog or later -- have a
look [here](https://developer.android.com/build/releases/gradle-plugin?hl=en#android_gradle_plugin_and_android_studio_compatibility)
for a compatibility matrix between versions of Gradle, AGP and Android Studio.

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