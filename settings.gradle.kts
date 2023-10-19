rootProject.name = "LeaderMobs"

fun createProject(name: String, path: String) {
    include(name)
    project(name).projectDir = file(path)
}

createProject(":main", "main")
createProject(":common", "common")

createProject(":mythicmobs-latest", "plugins/mythicmobs-latest")
createProject(":mythicmobs-legacy", "plugins/mythicmobs-legacy")

createProject(":platform-legacy", "platform/legacy")
createProject(":platform-paper-native", "platform/paper-native")