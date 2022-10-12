plugins {
    ear
    id("io.openliberty.tools.gradle.Liberty") version "3.5"
}

dependencies {
    deploy(project(path = ":roller-ejb"))
    deploy(project(path = ":roller-war", configuration = "archives"))
    testImplementation("commons-httpclient:commons-httpclient:3.1")
    testImplementation("junit:junit:4.13.1")
    libertyRuntime("com.ibm.websphere.appserver.runtime:wlp-javaee7:22.0.0.6")
}

ear {
    deploymentDescriptor {
        module("roller-ejb.jar", "ejb")
        webModule("roller-war.war", "/roller-war")
    }
}

liberty {
    server = io.openliberty.tools.gradle.extensions.ServerExtension()
    server.name = "rollerServer"

    server.deploy.apps = listOf(tasks["ear"])
    server.deploy.copyLibsDirectory = file("${project.buildDir}/libs")

    server.verifyAppStartTimeout = 30
    server.isLooseApplication = true
}

tasks.ear {
    archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
}

tasks["ear"].dependsOn(":roller-ejb:jar", ":roller-war:war", ":roller-lib:publishMavenPublicationToMavenLocal")