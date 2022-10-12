import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.gradle.node.npm.task.NpmTask
import io.openliberty.tools.gradle.extensions.ServerExtension
import io.openliberty.tools.gradle.extensions.FeatureExtension

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm") version "1.7.0"
    id("io.openliberty.tools.gradle.Liberty") version "3.4"
    id("com.github.node-gradle.node") version "3.3.0"
    war
}

val serverName by extra { "jmsServer" }

node {
    version.set("16.15.1")
    npmVersion.set("8.12.2")
    download.set(true)
    workDir.set(file("$rootDir/src/main/frontend/node"))
    nodeProjectDir.set(file("$rootDir/src/main/frontend"))
}

tasks.register<Delete>("cleanClient") {
    delete(fileTree("$rootDir/src/main/webapp").matching {
        exclude("**/WEB-INF/**")
    })
}

tasks.register<Delete>("cleanNpm") {
    dependsOn("clean")
    delete("$rootDir/src/main/frontend/node", "$rootDir/src/main/frontend/node_modules")
}

tasks.register("npmUpdate") {
    dependsOn("npm_update")
}

tasks.register<NpmTask>("installDependencies") {
    dependsOn("npmSetup")
    workingDir.set(File("$rootDir/src/main/frontend"))
    args.set(listOf("install"))
}

tasks.register<NpmTask>("buildStandaloneClient") {
    dependsOn("npmInstall")

    inputs.files(fileTree("${rootDir}/src/main/frontend/").matching {
        exclude("**/dist")
    }).withPropertyName("sourceFiles")
    outputs.dir("${rootDir}/src/main/frontend/dist").withPropertyName("outputDir")

    workingDir.set(File("$rootDir/src/main/frontend"))
    args.set(listOf("run", "build"))
}

tasks.register<Copy>("copyFrontend") {
    dependsOn("cleanClient", "buildStandaloneClient")

    from(fileTree("$rootDir/src/main/frontend/build"))
    into("$rootDir/src/main/webapp")
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    mavenCentral()
    mavenLocal()
}

val resourceAdapter by configurations.creating
val binaryScanner by configurations.creating

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    compileOnly("javax", "javaee-api", "6.0")
    compileOnly("sample", "roller-lib", "1.0")
    resourceAdapter("com.ibm.mq", "wmq.jmsra", "9.3.0.0")

    libertyRuntime("com.ibm.websphere.appserver.runtime", "wlp-kernel", "22.0.0.6")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    binaryScanner("com.ibm.websphere.appmod.tools:binary-app-scanner:22.0.0.3")
}

tasks.register<Copy>("copyResourceAdapters") {
    from(configurations.get("resourceAdapter"))
    into("${buildDir}/wlp/usr/shared/resources/wmq/")
    rename("wmq.jmsra-.*.rar", "wmq.jmsra.rar")
}

tasks.register<JavaExec>("runBinaryScanner") {
    dependsOn("war")
    group = "verification"

    classpath = binaryScanner
    workingDir = File(buildDir, "libs")

    val warFile = (tasks["war"] as War).archiveFile.get().asFile
    args(warFile.absolutePath, "--analyze", "--targetJava=java17", "--nobrowser", "--output=${buildDir.absolutePath}")
}


java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

liberty {
    server = ServerExtension()
    server.name = "uiServer"
    server.deploy.apps = listOf(tasks["war"])
    server.isLooseApplication = false
    server.features = FeatureExtension()
    server.features.acceptLicense = true
}

tasks["clean"].dependsOn("libertyStop")
tasks["libertyStart"].dependsOn("installFeature")
tasks["deploy"].dependsOn("copyResourceAdapters")
tasks["copyResourceAdapters"].dependsOn("libertyCreate")
tasks["npm_update"].dependsOn("libertyStop")
tasks["war"].dependsOn("copyFrontend")
//tasks["npmInstall"].outputs.upToDateWhen { false }
