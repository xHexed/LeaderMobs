plugins {
    id("base-conventions")
}

repositories {
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.lumine:Mythic-Dist:5.3.5")
    compileOnly(project(":leadermobs-main"))
}
