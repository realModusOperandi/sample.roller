plugins {
    `maven-publish`
}

description = "EJB Module"

dependencies {
    testImplementation("junit:junit:4.13.1")
    compileOnly("javax:javaee-api:7.0")
}

tasks.jar {
    archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}