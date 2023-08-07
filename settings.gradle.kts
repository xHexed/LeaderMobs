rootProject.name = "LeaderMobs"

include(":leadermobs-mythicmobs-latest")
project(":leadermobs-mythicmobs-latest").projectDir = file("plugins/MythicMobs-Latest")

include(":leadermobs-mythicmobs-legacy")
project(":leadermobs-mythicmobs-legacy").projectDir = file("plugins/MythicMobs-Legacy")

include(":leadermobs-main")
project(":leadermobs-main").projectDir = file("main")
