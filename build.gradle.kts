import org.gradle.jvm.tasks.Jar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Properties

val localPropertiesFile = project.rootProject.file("local.properties")
val localProperties = Properties()
if (localPropertiesFile.canRead())
    localProperties.load(localPropertiesFile.inputStream())

plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
    id("maven-publish")
    signing
    id("com.palantir.git-version") version "3.4.0"
    id("org.jetbrains.dokka") version "1.9.20"
    id("com.github.jk1.dependency-license-report") version "2.9"
    id("tech.yanand.maven-central-publish").version("1.3.0")
}

val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra

fun version(): String = versionDetails().run {
    if (commitDistance == 0 && isCleanTag && lastTag.matches(Regex("""\d+\.\d+\.\d+""")))
        version
    else (
            System.getenv("GITHUB_RUN_NUMBER")?.let { "ci-${branchName}-$it-${gitHash}" }
                ?: "dev-${branchName}-${
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("UTC")).format(Instant.now())
                }-${gitHash}"
            )
}

group = "com.eidu"
version = version()

kotlin {
    jvmToolchain(8)
}

licenseReport {
    allowedLicensesFile = File("$projectDir/allowed-licenses.json")
}

tasks.named("checkLicense") {
    // The checkLicense task does not declare this input itself, so we do it here. This ensures
    // that a modification of the file causes the checkLicense task to be re-evaluated.
    inputs.file("$projectDir/allowed-licenses.json")
}

tasks.named("check") {
    dependsOn("checkLicense")
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin JVM
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.3.20")

    // KotlinX Serialization
    api("org.jetbrains.kotlinx:kotlinx-serialization-core:1.11.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    // Logging
    api("org.slf4j:slf4j-api:2.0.18")

    // Learning Packages
    implementation("net.dongliu:apk-parser:2.6.10")

    // Unit Tests
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.1.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.1.3")
    testImplementation("io.mockk:mockk:1.14.11")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
}

tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn"
            )
        }
    }
    compileTestKotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
            )
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications.create<MavenPublication>("maven") {
        val javadocJar = tasks.register("javadocJar$name", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveBaseName.set("javadoc-${this@create.name}")
            from(tasks.dokkaHtml)
        }

        artifactId = rootProject.name
        version = project.version.toString()

        from(components["java"])
        artifact(javadocJar.get())
        artifact(sourcesJar)

        pom {
            name = rootProject.name
            description = "Support for reading EIDU learning packages"
            url = "https://github.com/EIDU/learning-packages"
            licenses {
                license {
                    name = "GNU Affero General Public License, version 3 (AGPLv3)"
                    url = "https://raw.githubusercontent.com/EIDU/learning-packages/main/LICENSE"
                }
            }
            developers {
                developer {
                    id = "berlix"
                    name = "Felix Engelhardt"
                    url = "https://github.com/berlix/"
                }
            }
            scm {
                connection = "scm:git:ssh://git@github.com/EIDU/learning-packages.git"
                developerConnection = "scm:git:ssh://git@github.com/EIDU/learning-packages.git"
                url = "https://github.com/EIDU/learning-packages"
            }
        }

        signing {
            useInMemoryPgpKeys(
                System.getenv("SIGNING_KEY_ID"),
                System.getenv("SIGNING_KEY"),
                System.getenv("SIGNING_PASSWORD")
            )
            sign(this@create)
        }
    }
}

mavenCentral {
    authToken.set(Base64.getEncoder().encodeToString("${System.getenv("MAVEN_CENTRAL_USERNAME")}:${System.getenv("MAVEN_CENTRAL_PASSWORD")}".toByteArray()))
    publishingType.set("USER_MANAGED")
    maxWait.set(300)
}

ProcessBuilder("git config --local core.hooksPath git-hooks".split(" ")).start()
