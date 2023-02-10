plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("net.kyori.indra:net.kyori.indra.gradle.plugin:3.0.1")
    implementation("net.kyori.indra.git:net.kyori.indra.git.gradle.plugin:3.0.1")
}
