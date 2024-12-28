val ktor = "2.3.12"
val kotlin = "2.1.0"

plugins {
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "2.3.12"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.0"
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

    implementation("org.apache.commons:commons-csv:1.12.0")
    implementation("ch.qos.logback:logback-classic:1.5.15")

    implementation("io.lettuce:lettuce-core:6.5.1.RELEASE")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin")
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}
