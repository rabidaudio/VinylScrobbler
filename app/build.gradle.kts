import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    kotlin("kapt")
    id("com.getkeepsafe.dexcount")
}

val appVersion = "1.0"

fun dateVersionCode(): Int {
    return DateTimeFormatter.ofPattern("yyyyMMdd")
        .format(Instant.now().atZone(ZoneId.systemDefault())).toInt()
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")
    defaultConfig {
        applicationId = "audio.rabid.vinylscrobbler"
        minSdkVersion(26)
        targetSdkVersion(29)
        versionCode = dateVersionCode()
        versionName = appVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "CONTACT_EMAIL", "\"charles@rabidaudio.com\"")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mapOf(
                    "room.incremental" to "true",
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
    }

    android.buildFeatures.viewBinding = true
    androidExtensions.isExperimental = true
}

dexcount {
    includeFieldCount = true
    includeTotalMethodCount = false
    orderByMethodCount = true
    enabled = ("true" == System.getenv("CI") || "true" == System.getenv("DEXCOUNT"))
}

repositories {
    // used by contour
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    val kotlinVersion = rootProject.extra.get("kotlin_version")
    val roomVersion = rootProject.extra.get("room_version")
    val viewModelVersion = rootProject.extra.get("androidx_viewmodel_version")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

    // Support
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta4")
    implementation("androidx.lifecycle:lifecycle-common-java8:$viewModelVersion")
    kapt("androidx.lifecycle:lifecycle-compiler:$viewModelVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$viewModelVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$viewModelVersion")
    implementation("com.google.android.material:material:1.2.0-alpha05")

    // Retrofit + Moshi - API
    implementation("com.squareup.retrofit2:retrofit:2.6.2")
    implementation("com.squareup.moshi:moshi:1.9.1")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.1")
    implementation("com.squareup.retrofit2:converter-moshi:2.6.2")

    // Room - Database
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    // Kaddi - DI
    implementation("com.github.rabidaudio.kaddi:kaddi-dsl:3aee32b")
//    implementation("audio.rabid.kaddi:kaddi-dsl:0.0.1")

    // UI Utils
    implementation("com.squareup.picasso:picasso:2.71828")
    // implementation("app.cash.contour:contour:0.1.6")

    // Debugging Utils
    implementation("com.github.fixdauto:android-logger:1.1.0")
    debugImplementation("com.facebook.flipper:flipper:0.30.0")
    debugImplementation("com.facebook.soloader:soloader:0.8.0")
    debugImplementation("com.facebook.flipper:flipper-network-plugin:0.30.0")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.6.3")
    debugImplementation("com.facebook.flipper:flipper-leakcanary-plugin:0.30.0")

    // Testing
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.8")
    testImplementation("org.spekframework.spek2:spek-runner-junit5:2.0.8")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.1")

    testImplementation("com.winterbe:expekt:0.5.0")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.2")
}
