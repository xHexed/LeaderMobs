plugins {
    id("base-conventions")
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(project(":platform-legacy"))
    compileOnly(project(":platform-paper-native"))

    implementation("com.tchristofferson:ConfigUpdater:2.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.4")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    //testImplementation("org.yaml:snakeyaml:1.15")
    //testImplementation("commons-lang:commons-lang:2.6")
    testImplementation("org.testng:testng:7.10.2")
}

configurations.testImplementation.get().apply {
    extendsFrom(configurations.compileOnly.get())
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
    compileTestJava {
        sourceCompatibility = "17"
    }
    test {
        useTestNG()
    }
}