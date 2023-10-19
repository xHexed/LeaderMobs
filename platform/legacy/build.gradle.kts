plugins {
    id("base-conventions")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly(project(":common"))

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") {
        isTransitive = false
    }
    implementation("net.kyori:adventure-platform-bukkit:4.3.1")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")
}

tasks {
    shadowJar {
        relocate("com.bgsoftware.common.config", "com.github.xhexed.leadermobs.config")
        relocate("net.kyori.adventure", "com.github.xhexed.leadermobs.adventure")
        relocate("net.kyori.examination", "com.github.xhexed.leadermobs.adventure.examination")
        exclude("META-INF/**")
    }
}