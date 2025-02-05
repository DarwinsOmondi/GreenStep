// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}


subprojects {
    afterEvaluate {
        configurations.all {
            resolutionStrategy.eachDependency {
                if (requested.group == "org.bouncycastle" && requested.name.contains("jdk18on")) {
                    useVersion("1.78.1") // Ensure only one version is used
                }
            }
        }
    }
}
