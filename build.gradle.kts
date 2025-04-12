val ktor = "3.1.2"
val kotlin = "2.1.20"

plugins {
    kotlin("jvm") version "2.1.20"
    id("io.ktor.plugin") version "3.0.3"
    kotlin("plugin.serialization") version "2.1.20"
    id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

group = "westmeijer.oskar"
version = "1.0.0"

application {
    mainClass.set("westmeijer.oskar.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

ktor {
    fatJar {
        archiveFileName.set("app.jar")
    }
}

repositories {
    mavenCentral()
}

tasks.check {
    finalizedBy(tasks.koverHtmlReport, tasks.koverXmlReport, tasks.koverLog)
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor")
    implementation("io.ktor:ktor-server-cors:$ktor")
    implementation("io.ktor:ktor-server-config-yaml:$ktor")

    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")

    implementation("io.ktor:ktor-client-content-negotiation:$ktor")

    implementation("io.ktor:ktor-client-core:$ktor")
    implementation("io.ktor:ktor-client-cio:$ktor")

    implementation("org.apache.commons:commons-csv:1.13.0")
    implementation("ch.qos.logback:logback-classic:1.5.16")

    implementation("io.lettuce:lettuce-core:6.5.5.RELEASE")

    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.13")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin")
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
}
