import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    base
    id("base-conventions")
}

allprojects {
    group = "com.github.xhexed"
    version = "2.2.1"
}

tasks {
    jar {
        dependsOn(shadowJar)
        enabled = false
    }
    shadowJar {
        subprojects.forEach { include ->
            val shadowJarTask = include.tasks.named<ShadowJar>("shadowJar").get()
            dependsOn(shadowJarTask)
            //dependsOn(include.tasks.withType<Jar>())
            from(zipTree(shadowJarTask.archiveFile))
        }
        archiveFileName.set("LeaderMobs-${project.version}.jar")
    }
}




