plugins {
    kotlin("jvm")
    application
}

group = "westmeijer.oskar"

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:5.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.5.1")
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
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    archiveBaseName.set("maps-scheduler")
    manifest {
        attributes(
            "Main-Class" to "westmeijer.oskar.MainKt"
        )
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}