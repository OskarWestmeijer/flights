val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val apache_commons: String by project
val json_assert: String by project
val lettuce_core: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.8"
    kotlin("plugin.serialization") version "1.9.22"
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
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
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")

    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

    implementation("org.apache.commons:commons-csv:$apache_commons")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("io.lettuce:lettuce-core:$lettuce_core")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.skyscreamer:jsonassert:$json_assert")
}
