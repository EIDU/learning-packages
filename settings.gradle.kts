pluginManagement {
    val properties: java.util.Properties by lazy {
        java.util.Properties().apply { load(rootProject.projectDir.resolve("local.properties").inputStream()) }
    }

    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "learning-packages"
