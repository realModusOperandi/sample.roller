description = "EJB Module"

dependencies {
    testImplementation("junit:junit:4.13.1")
    compileOnly("javax:javaee-api:7.0")
    compileOnly(project(":roller-lib"))
}

tasks.jar {
    archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}