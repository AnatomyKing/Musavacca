import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.Delete
import org.gradle.jvm.tasks.Jar

plugins {
    id("net.neoforged.moddev")
    // id("maven-publish")
    // id("me.modmuss50.mod-publish-plugin")
}

val sc = stonecutter

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava = when {
    sc.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

val usesClientData = sc.current.parsed >= "1.21.4"

val rootResourcesDir = rootProject.layout.projectDirectory.dir("src/main/resources")

val generatedResourcesDir = layout.projectDirectory.dir("src/generated/resources")
val versionResourcesDir = layout.projectDirectory.dir("src/main/resources")

val sharedResourcesDirProvider = layout.buildDirectory.dir("stonecutterSharedResources")

val syncSharedResources = tasks.register<Sync>("syncSharedResources") {
    from(rootResourcesDir)
    into(sharedResourcesDirProvider)
    includeEmptyDirs = false
}

val sharedResources = files(sharedResourcesDirProvider).builtBy(syncSharedResources)

val cleanGeneratedResources = tasks.register<Delete>("cleanGeneratedResources") {
    group = "moddev"
    description = "Deletes versions/<mc>/src/generated/resources before datagen."
    delete(generatedResourcesDir.asFile)
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    // your deps
}

neoForge {
    version = property("deps.neoforge") as String

    mods {
        register(property("mod.id") as String) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            gameDirectory = file("../../run/")
            client()
        }

        register("server") {
            gameDirectory = file("../../run/")
            server()
        }

        register("data") {
            gameDirectory = file("../../run/")

            if (usesClientData) clientData() else data()

            fun existing(dirPath: String) {
                programArguments.addAll(listOf("--existing", dirPath))
            }

            programArguments.addAll(
                listOf(
                    "--mod", property("mod.id") as String,
                    "--all",
                    "--output", generatedResourcesDir.asFile.absolutePath
                )
            )

            existing(versionResourcesDir.asFile.absolutePath)

            existing(sharedResources.asPath)

            // if you reference other mods' models/textures in datagen:
            // programArguments.addAll(listOf("--existing-mod", "minecraft"))
            // programArguments.addAll(listOf("--existing-mod", "someothermod"))
        }
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

sourceSets {
    named("main") {
        resources.setSrcDirs(
            listOf(
                versionResourcesDir.asFile,

                generatedResourcesDir.asFile,

                sharedResources
            )
        )
    }
}

tasks {

    named<Jar>("sourcesJar") {
        dependsOn(syncSharedResources)
    }

    withType<ProcessResources>().configureEach {
        dependsOn(syncSharedResources)

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    processResources {
        inputs.property("id", project.property("mod.id"))
        inputs.property("name", project.property("mod.name"))
        inputs.property("version", project.property("mod.version"))
        inputs.property("minecraft", project.property("mod.mc_dep"))

        fun prop(name: String) = project.property(name)
            .toString()
            .trim()
            .replace("\r", "")
            .replace("\n", "")

        val props = mapOf(
            "id" to prop("mod.id"),
            "name" to prop("mod.name"),
            "version" to prop("mod.version"),
            "minecraft" to prop("mod.mc_dep")
        )

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(props)
        }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
        dependsOn(syncSharedResources)
    }

    named("runClient") {
        dependsOn("stonecutterGenerate")
        dependsOn(syncSharedResources)
    }

    named("runServer") {
        dependsOn("stonecutterGenerate")
        dependsOn(syncSharedResources)
    }

    named("runData") {
        dependsOn("stonecutterGenerate")
        dependsOn(syncSharedResources)

        dependsOn(cleanGeneratedResources)
    }

    register("runDatagen") {
        group = "moddev"
        description = "Runs datagen for this Minecraft version into versions/<mc>/src/generated/resources."
        dependsOn("runData")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}



/*
// Publishes builds to Modrinth and Curseforge with changelog from the CHANGELOG.md file
publishMods {
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.sourcesJar.map { it.archiveFile.get() })
    displayName = "${property("mod.name")} ${property("mod.version")} for ${property("mod.mc_title")}"
    version = property("mod.version") as String
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add("neoforge")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null
        || providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(property("mod.mc_targets").toString().split(' '))
    }

    curseforge {
        projectId = property("publish.curseforge") as String
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        minecraftVersions.addAll(property("mod.mc_targets").toString().split(' '))
    }
}
 */
/*
// Publishes builds to a maven repository under `com.example:template:0.1.0+mc`
publishing {
    repositories {
        maven("https://maven.example.com/releases") {
            name = "myMaven"
            // To authenticate, create `myMavenUsername` and `myMavenPassword` properties in your Gradle home properties.
            // See https://stonecutter.kikugie.dev/wiki/tips/properties#defining-properties
            credentials(PasswordCredentials::class.java)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "${property("mod.group")}.${property("mod.id")}"
            artifactId = property("mod.id") as String
            version = project.version

            from(components["java"])
        }
    }
}
 */
