import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission

plugins {
    id("cc.mewcraft.common")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "cc.mewcraft"
version = "1.18".decorateVersion()
description = "A plugin consisting of many small features"

dependencies {
    // 3rd party plugins
    compileOnly("net.luckperms", "api", "5.4")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7") { isTransitive = false }
    compileOnly("me.clip", "placeholderapi", "2.11.2") { isTransitive = false }
    compileOnly("com.github.LoneDev6", "API-ItemsAdder", "3.2.5")

    // To be shaded
    implementation("net.wesjd", "anvilgui", "1.6.3-SNAPSHOT")
    implementation("com.google.inject", "guice", "5.1.0")
}

bukkit {
    main = "cc.mewcraft.mewutils.MewUtils"
    name = "MewUtils"
    version = "${project.version}"
    description = "Provides tiny features that can't make a big project"
    apiVersion = "1.17"
    authors = listOf("Nailm")
    depend = listOf("helper", "MewCore", "Vault")
    softDepend = listOf("PlaceholderAPI", "ItemsAdder")
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
        archiveClassifier.set("nonshade")
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        val path = "cc.mewcraft.shade."
        relocate("net.wesjd.anvilgui", path + "anvilgui")
        relocate("com.google.inject", path + "inject")
        archiveFileName.set("MewUtils-${project.version}.jar")
    }
    register("deployJar") {
        doLast {
            exec {
                commandLine("rsync", shadowJar.get().archiveFile.get().asFile.absoluteFile, "dev:data/dev/jar")
            }
        }
    }
    register("deployJarFresh") {
        dependsOn(build)
        finalizedBy(named("deployJar"))
    }
}

fun lastCommitHash(): String = indraGit.commit()?.name?.substring(0, 7) ?: error("Could not determine commit hash")
fun String.decorateVersion(): String = if (endsWith("-SNAPSHOT")) "$this-${lastCommitHash()}" else this