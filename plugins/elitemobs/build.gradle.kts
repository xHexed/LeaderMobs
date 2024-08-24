plugins {
    id("base-conventions")
}

repositories {
    mavenCentral()
    //EliteMobs
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies{
    //EliteMobs
    compileOnly("com.magmaguy:EliteMobs:8.7.4-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly(project(":main"))
}
