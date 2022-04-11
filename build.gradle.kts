plugins {
    id("java")
    kotlin("jvm") version "1.6.20"
}

allprojects {
    group = "network.hoz"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.hoznet.dev/public")
        maven("https://repo.screamingsandals.org/public")
    }
}


subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("idea")
        plugin("java-library")
    }

    dependencies {
        implementation(kotlin("kotlin-reflect"))

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    }

    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }
}