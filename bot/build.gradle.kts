plugins {
    alias(libs.plugins.jvm)
    application
    id("org.springframework.boot") version "3.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.0")
    implementation("eu.vendeli:telegram-bot:3.5.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.0")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose:3.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.2.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    mainClass.set("memorizer.SpringBot")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
