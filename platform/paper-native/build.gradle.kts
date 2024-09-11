plugins {
    id("base-conventions")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    compileOnly(project(":common"))

    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
}

