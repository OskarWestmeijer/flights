plugins {
    kotlin("jvm")
}

group = "westmeijer.oskar"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.lettuce:lettuce-core:6.3.1.RELEASE")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("ch.qos.logback:logback-classic:1.2.6")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}