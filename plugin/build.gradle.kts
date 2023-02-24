import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission

plugins {
    id("cc.mewcraft.common")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "cc.mewcraft"
version = "1.20.0".decorateVersion()
description = "A plugin consisting of many small features"

dependencies {
    // 3rd party plugins
    compileOnly("net.luckperms", "api", "5.4")
    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7") {
        exclude("net.bytebuddy")
    }
    compileOnly("me.clip", "placeholderapi", "2.11.2") {
        exclude("org.jetbrains")
    }
    compileOnly("com.github.LoneDev6", "API-ItemsAdder", "3.2.5")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
    compileOnly("net.essentialsx", "EssentialsX", "2.19.0") { isTransitive = false }

    // To be shaded
    implementation(project(":common"))
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
    depend = listOf("helper", "MewCore")
    softDepend = listOf("Vault", "PlaceholderAPI", "ItemsAdder", "ProtocolLib", "Essentials")
    permissions {
        register("mew.admin") {
            description = "Permission nodes for operators."
            default = Permission.Default.OP
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
        // TODO with paper plugin API, we might don't need shadow anymore

        archiveFileName.set("MewUtils-${project.version}.jar")

        val path = "cc.mewcraft.shade."

        // AnvilGui
        relocate("net.wesjd.anvilgui", path + "anvilgui")

        // Google Guice
        relocate("javax", path + "javax")
        relocate("com.google", path + "google")
        relocate("org.aopalliance", path + "aopalliance")

        minimize()
    }
    processResources {
        filesMatching("**/paper-plugin.yml") {
            expand(
                mapOf(
                    "version" to "${project.version}",
                    "description" to project.description
                )
            )
        }
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