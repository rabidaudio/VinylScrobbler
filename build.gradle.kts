// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra.set("kotlin_version", "1.3.71")
    extra.set("room_version", "2.2.1")
    extra.set("androidx_viewmodel_version", "2.2.0")

    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-beta01")
//        classpath(kotlin("gradle-plugin"))
        val kotlinVersion = rootProject.extra.get("kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        classpath("com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.5")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.5.1.0")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven(url = "https://jitpack.io")
    }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}
