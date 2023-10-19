plugins {
    id("base-conventions")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    maven("https://repo.bg-software.com/repository/common/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(project(":platform-legacy"))
    compileOnly(project(":platform-paper-native"))

    implementation("com.bgsoftware.common.config:CommentedConfiguration:1.0.3")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") {
        isTransitive = false
    }
    compileOnly("me.clip:placeholderapi:2.11.4")

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

tasks {
    processResources {
        filesMatching(listOf("plugin.yml")) {
            expand("version" to project.version)
        }
    }
    shadowJar {
        relocate("com.bgsoftware.common.config", "com.github.xhexed.leadermobs.config")
    }
}