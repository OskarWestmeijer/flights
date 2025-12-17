plugins {
    kotlin("jvm") version "2.3.0"
    id("io.ktor.plugin") version "3.3.3"
    kotlin("plugin.serialization") version "2.3.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.4"
}

kotlin {
    // TODO 14.12.25: "Kotlin does not yet support 25 JDK target, falling back to Kotlin JVM_24 JVM target"
    jvmToolchain(24)
}

group = "westmeijer.oskar"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-config-yaml")

    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    implementation("io.ktor:ktor-client-content-negotiation")

    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")

    implementation("org.apache.commons:commons-csv:1.14.1")
    implementation("ch.qos.logback:logback-classic:1.5.22")

    implementation("io.lettuce:lettuce-core:7.2.1.RELEASE")

    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.13")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}

application {
    mainClass.set("westmeijer.oskar.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

tasks.check {
    finalizedBy(tasks.koverHtmlReport, tasks.koverXmlReport, tasks.koverLog)
    finalizedBy(tasks.buildFatJar)
}
