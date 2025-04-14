plugins {
    kotlin("jvm")
    application
}

group = "westmeijer.oskar"

repositories {
    mavenCentral()
}

dependencies {
    implementation("redis.clients:jedis:5.2.0")

    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("com.typesafe:config:1.4.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    archiveBaseName.set("flights-scheduler")
    manifest {
        attributes(
            "Main-Class" to "westmeijer.oskar.MainKt"
        )
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}