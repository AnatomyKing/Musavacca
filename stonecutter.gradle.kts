import org.gradle.api.Project
import java.io.File

plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.119" apply false
}

stonecutter active "1.21.8"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("mod.version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod.id") != "template"
}

fun readActiveProjectName(): String {
    val text = file("stonecutter.gradle.kts").readText()
    val m = Regex("""stonecutter\s+active\s+"([^"]+)"""").find(text)
        ?: error("Could not find `stonecutter active \"...\"` in stonecutter.gradle.kts")
    return m.groupValues[1]
}

val versionsDir: File = rootProject.layout.projectDirectory
    .dir("versions")
    .asFile
    .canonicalFile

fun isVersionNode(p: Project): Boolean =
    p.projectDir.parentFile?.canonicalFile == versionsDir

val runDatagenAll = tasks.register("runDatagenAll") {
    group = "stonecutter"
    description = "Runs datagen for ALL Stonecutter version projects."
}

gradle.projectsEvaluated {
    val nodes = rootProject.allprojects.filter(::isVersionNode)

    runDatagenAll.configure {
        doFirst {
            logger.lifecycle(
                "runDatagenAll -> ${nodes.size} version projects: " +
                        nodes.joinToString { it.path }
            )
        }
        dependsOn(nodes.map { "${it.path}:runDatagen" })
    }
}

tasks.register("runDatagenActive") {
    group = "stonecutter"
    description = "Runs datagen only for the CURRENT active Stonecutter project."
    val active = readActiveProjectName()
    dependsOn(":$active:runDatagen")
}

tasks.register("runClientActive") {
    group = "stonecutter"
    description = "Runs the client only for the CURRENT active Stonecutter project."
    val active = readActiveProjectName()
    dependsOn(":$active:runClient")
}

tasks.register("runServerActive") {
    group = "stonecutter"
    description = "Runs the server only for the CURRENT active Stonecutter project."
    val active = readActiveProjectName()
    dependsOn(":$active:runServer")
}
