pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.1-beta.1"
}

stonecutter {
    create(rootProject) {
        versions(
            "1.21.1",
            "1.21.3",
            "1.21.4",
            "1.21.5",
            "1.21.8",
            "1.21.10"
        )
        vcsVersion = "1.21.8"
    }
}

rootProject.name = "Musavacca"
