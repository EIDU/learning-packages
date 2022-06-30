pluginManagement {
    val properties: java.util.Properties by lazy {
        java.util.Properties().apply { load(rootProject.projectDir.resolve("local.properties").inputStream()) }
    }

    repositories {
        gradlePluginPortal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/EIDU/learning-packages")
            credentials {
                username = System.getenv("READPACKAGES_GITHUB_USER")
                    ?: System.getenv("GITHUB_READPACKAGES_USER")
                            ?: properties.getProperty("githubReadPackagesUser")
                password = System.getenv("READPACKAGES_GITHUB_TOKEN")
                    ?: System.getenv("GITHUB_READPACKAGES_TOKEN")
                            ?: properties.getProperty("githubReadPackagesToken")
            }
        }
    }
}

rootProject.name = "learning-packages"
