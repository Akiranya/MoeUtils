/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
    id("net.kyori.indra")
    id("net.kyori.indra.git")
}

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://papermc.io/repo/repository/maven-public/") {
        content {
            includeGroup("io.papermc.paper")
            includeGroup("net.md-5")
        }
    }
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.MilkBowl")
            includeGroup("com.github.LoneDev6")
        }
    }
    maven("https://repo.lucko.me/") {
        content {
            includeGroup("me.lucko")
        }
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        content {
            includeGroup("me.clip")
        }
    }
    maven("https://repo.codemc.io/repository/maven-snapshots/") {
        content {
            includeGroup("net.wesjd")
        }
    }
    maven("https://repo.dmulloy2.net/repository/public/") {
        content {
            includeGroup("com.comphenix.protocol")
        }
    }
    maven("https://repo.essentialsx.net/releases/") {
        content {
            includeGroup("net.essentialsx")
        }
    }
}

dependencies {
    // Server API
    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")

    // Plugin libs
    compileOnly("cc.mewcraft", "MewCore", "5.13.1")
    compileOnly("me.lucko", "helper", "5.6.13")

    // To be shaded
    implementation("com.google.inject", "guice", "5.1.0")
}

indra {
    javaVersions().target(17)
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
