pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("http://maven.aliyun.com/nexus/content/repositories/releases/") {
            name = "aliyun"
            //一定要添加这个配置
            isAllowInsecureProtocol = true
        }
        jcenter()
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.60.5"
}

rootProject.name = "S1-Next"
include(":app")
include(":library")
include(":JKeyboardPanelSwitch")