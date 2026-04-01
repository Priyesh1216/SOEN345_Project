plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.9.0"
    id("jacoco")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.soen345_project"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.soen345_project"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))

    implementation("com.google.firebase:firebase-database")

    implementation("com.google.firebase:firebase-auth")

    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("androidx.datastore:datastore-preferences:1.2.0")
    implementation("androidx.datastore:datastore:1.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

val jacocoTestReport by tasks.registering(JacocoReport::class) {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val debugTree = fileTree("${layout.buildDirectory.get().asFile}/intermediates/javac/debug/classes") {
        exclude(
            "**/R.class",
            "**/R\$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*"
        )
    }
    val kotlinDebugTree = fileTree("${layout.buildDirectory.get().asFile}/tmp/kotlin-classes/debug") {
        exclude(
            "**/R.class",
            "**/R\$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*"
        )
    }

    val mainSrc = "${project.projectDir}/src/main/java"
    val kotlinSrc = "${project.projectDir}/src/main/kotlin"

    classDirectories.setFrom(files(debugTree, kotlinDebugTree))
    sourceDirectories.setFrom(files(mainSrc, kotlinSrc))
    executionData.setFrom(fileTree("${layout.buildDirectory.get().asFile}").include(
        "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
    ))
}