import org.gradle.jvm.tasks.Jar
import java.io.ByteArrayOutputStream
import java.util.Properties

val localPropertiesFile = project.rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.canRead())
    localProperties.load(localPropertiesFile.inputStream())

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("maven-publish")
    id("com.eidu.vulnerability-reporter") version "1.+"
}

fun run(command: String): String {
    ByteArrayOutputStream().use { output ->
        exec {
            commandLine("sh", "-c", command)
            standardOutput = output
        }
        return output.toString().trim()
    }
}

fun version(): String = System.getenv("GITHUB_RUN_NUMBER")?.let { runNumber ->
    "1.0.$runNumber" + (
            run("git rev-parse --abbrev-ref HEAD").takeIf { it != "main" }?.let { "-$it" } ?: ""
            )
} ?: "snapshot"

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/EIDU/learning-packages")
        credentials {
            username = System.getenv("READPACKAGES_GITHUB_USER")
                ?: System.getenv("GITHUB_READPACKAGES_USER")
                        ?: localProperties.getProperty("githubReadPackagesUser")
            password = System.getenv("READPACKAGES_GITHUB_TOKEN")
                ?: System.getenv("GITHUB_READPACKAGES_TOKEN")
                        ?: localProperties.getProperty("githubReadPackagesToken")
        }
    }
}

dependencies {
    // Kotlin JVM
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")

    // KotlinX Serialization
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    // Logging
    api("org.slf4j:slf4j-api:1.7.36")

    // Unit Tests
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("com.willowtreeapps.assertk:assertk:0.25")

    // Ktlint Rules
    ktlintRuleset("com.eidu:ktlint-rules:1.0.8")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xopt-in=kotlin.RequiresOptIn"
            )
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xopt-in=kotlin.RequiresOptIn",
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/EIDU/learning-packages")
            credentials {
                username = System.getenv("GITHUB_USER")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.eidu"
            artifactId = "learning-packages"
            version = version()

            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

ProcessBuilder("git config --local core.hooksPath git-hooks".split(" ")).start()
