rootProject.name = "roller"
include(":roller-ejb")
include(":roller-war")
include(":roller-ear")
include(":roller-lib")

project(":roller-ejb").projectDir = File("$rootDir/roller-ejb")
project(":roller-war").projectDir = File("$rootDir/roller-war")
project(":roller-ear").projectDir = File("$rootDir/roller-ear")
project(":roller-lib").projectDir = File("$rootDir/roller-lib")