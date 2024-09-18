plugins {
    id("base-conventions")
}

repositories {
    mavenCentral()
    maven("https://repo.magmaguy.com/releases")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies{
    compileOnly("com.magmaguy:EliteMobs:9.1.10")
    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly(project(":main"))
}
