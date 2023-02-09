import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission

plugins {
    `java-library`
    `maven-publish`
    val indraVersion = "3.0.1"
    id("net.kyori.indra") version indraVersion
    id("net.kyori.indra.git") version indraVersion
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

group = "cc.mewcraft"
description = "MewUtils"
version = "1.18".decorateVersion()

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
}

dependencies {
    // Server API
    compileOnly("io.papermc.paper", "paper-api", "1.19.3-R0.1-SNAPSHOT")

    // Plugin libs
    compileOnly("cc.mewcraft", "MewCore", "5.11")
    compileOnly("me.lucko", "helper", "5.6.13") { isTransitive = false }

    // 3rd party plugins
    compileOnly("net.luckperms", "api", "5.4")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7") { isTransitive = false }
    compileOnly("me.clip", "placeholderapi", "2.11.2") { isTransitive = false }
}

bukkit {
    main = "cc.mewcraft.mewutils.MewUtils"
    name = "MewUtils"
    version = "${project.version}"
    description = "Provides tiny features that can't make a big project"
    apiVersion = "1.17"
    authors = listOf("Nailm")
    depend = listOf("helper", "MewCore", "Vault")
    softDepend = listOf("PlaceholderAPI")
    permissions {
        register("mew.admin") {
            description = "Permission nodes for operators."
            default = Permission.Default.OP
            children = listOf(
                "mew.reload",
                "mew.magic.reset",
                "mew.magic.status"
            )
        }
        register("mew.user") {
            description = "Basic permission nodes for players."
            default = Permission.Default.OP
            children = listOf(
                "mew.magic.time",
                "mew.magic.weather"
            )
        }
    }
}

tasks {
    jar {
        archiveFileName.set("MewUtils-${project.version}.jar")
    }
    register("deployJar") {
        doLast {
            exec {
                commandLine("rsync", jar.get().archiveFile.get().asFile.absoluteFile, "dev:data/dev/jar")
            }
        }
    }
    register("deployJarFresh") {
        dependsOn(build)
        finalizedBy(named("deployJar"))
    }
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

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7) ?: error("Could not determine commit hash")
fun String.decorateVersion(): String = if (endsWith("-SNAPSHOT")) "$this-${lastCommitHash()}" else this
