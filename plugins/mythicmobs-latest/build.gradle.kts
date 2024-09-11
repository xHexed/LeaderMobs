plugins {
    id("base-conventions")
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://mvn.lumine.io/repository/maven-public/")
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    compileOnly("io.lumine:Mythic-Dist:5.4.1")
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly(project(":main"))
}
