plugins {
    id("base-conventions")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://mvn.lumine.io/repository/maven-public/")
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT") {
        isTransitive = false
    }
    compileOnly("io.lumine:Mythic-Dist:5.6.2")
    compileOnly(project(":main"))
}
