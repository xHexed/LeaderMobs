plugins {
    id("base-conventions")
}

repositories {
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.lumine.xikage:MythicMobs:4.12.0")
    compileOnly(project(":leadermobs-main"))
}
