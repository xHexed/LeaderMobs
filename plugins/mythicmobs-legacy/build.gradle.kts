plugins {
    id("base-conventions")
}

repositories {
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.lumine.xikage:MythicMobs:4.12.0")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") {
        isTransitive = false
    }
    compileOnly(project(":main"))
}
