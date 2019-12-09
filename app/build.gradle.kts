import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

plugins {
    id("com.android.application")
    id("kotlin-android")
    kotlin("kapt")
    id("okreplay")
    id("com.getkeepsafe.dexcount")
}

val appVersion = "1.0"

fun dateVersionCode(): Int {
    return DateTimeFormatter.ofPattern("yyyyMMdd")
        .format(Instant.now().atZone(ZoneId.systemDefault())).toInt()
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.1")
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

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")

    // Support
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0-rc02")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0-rc02")

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

    // Contour - UI
    implementation("app.cash.contour:contour:0.1.5-SNAPSHOT")
    // DSL for shape drawables
    implementation("com.github.infotech-group:android-drawable-dsl:0.3.0")

    // UI Utils
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.github.arimorty:floatingsearchview:2.1.1")

    // Debugging Utils
    debugImplementation("com.facebook.flipper:flipper:0.27.0")
    debugImplementation("com.facebook.soloader:soloader:0.8.0")
    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.6.3")

    // Testing
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.8")
    testImplementation("org.spekframework.spek2:spek-runner-junit5:2.0.8")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.1")

    debugImplementation("com.airbnb.okreplay:okreplay:1.5.0")
    releaseImplementation("com.airbnb.okreplay:noop:1.5.0")
    androidTestImplementation("com.airbnb.okreplay:espresso:1.5.0")

    testImplementation("com.winterbe:expekt:0.5.0")
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.2")
}
