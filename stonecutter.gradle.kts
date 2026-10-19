plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.148" apply false
}

stonecutter active "1.21.8"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("mod.version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod.id") != "template"
}

stonecutter tasks {
    order("runData")
}

tasks.register("runDatagenAll") {
    group = "stonecutter"
    description = "Runs datagen for every registered Minecraft version."
    dependsOn(stonecutter.tasks.named("runDatagen"))
}

tasks.register("compileAll") {
    group = "stonecutter"
    description = "Compiles the Java sources for every registered Minecraft version."
    dependsOn(stonecutter.tasks.named("compileJava"))
}

tasks.register("buildAll") {
    group = "stonecutter"
    description = "Builds and collects every version. Run runDatagenAll first."
    dependsOn(stonecutter.tasks.named("buildAndCollect"))
}

val activeProject = requireNotNull(stonecutter.current) {
    "Set an active Stonecutter version first."
}.project

for ((alias, target) in mapOf(
    "runDatagenActive" to "runDatagen",
    "runClientActive" to "runClient",
    "runServerActive" to "runServer"
)) {
    tasks.register(alias) {
        group = "stonecutter"
        description = "Runs $target for the active Stonecutter version."
        dependsOn(":$activeProject:$target")
    }
}
