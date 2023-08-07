import gradle.kotlin.dsl.accessors._21d323b7f457c7b1c7c3ec9d4211e425.shadowJar

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

repositories {
    mavenLocal()
    mavenCentral()

    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.20-R0.1-SNAPSHOT")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    jar {
        enabled = false
    }
    shadowJar {
        minimize()
    }
}

