// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val objectboxVersion by extra("4.3.0")
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
}