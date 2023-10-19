plugins {
    `java-library`
    id("com.github.johnrengelman.shadow")
}

java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
    jar {
        enabled = false
    }
    shadowJar {
        minimize()
    }
}

