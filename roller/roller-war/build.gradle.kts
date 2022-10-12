plugins {
    war
}

tasks.jar {
    enabled = false
}
description = "WAR Module"

dependencies {
    testImplementation("junit:junit:4.13.1")
    providedCompile("javax:javaee-api:7.0")
    providedCompile(project(":roller-ejb"))
    providedCompile(project(":roller-lib"))
}

tasks.war {
    archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}