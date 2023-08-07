plugins {
    id("base-conventions")
}

repositories {
    maven("https://repo.bg-software.com/repository/common/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    implementation("com.bgsoftware.common.config:CommentedConfiguration:1.0.3")
    compileOnly("me.clip:placeholderapi:2.11.3")
}

tasks {
    processResources {
        filesMatching(listOf("plugin.yml")) {
            expand("version" to project.version)
        }
    }
}

tasks {
    shadowJar {
        relocate("com.bgsoftware.common.config", "com.github.xhexed.leadermobs.config")
    }
}