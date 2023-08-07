plugins {
    id("base-conventions")
}

repositories {
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.lumine.xikage:MythicMobs:4.8.0")
    compileOnly(project(":leadermobs-main"))
}
