plugins {
    kotlin("jvm")
}

group = "westmeijer.oskar"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.lettuce:lettuce-core:6.3.1.RELEASE")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("com.typesafe:config:1.4.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.jar {
    archiveBaseName.set("maps-scheduler")
    manifest {
        attributes(
            "Main-Class" to "Main.kt"
        )
    }
}