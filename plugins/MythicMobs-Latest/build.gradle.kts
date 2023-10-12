plugins {
    id("base-conventions")
}

repositories {
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.lumine:Mythic-Dist:5.4.1")
    compileOnly(project(":leadermobs-main"))
}
