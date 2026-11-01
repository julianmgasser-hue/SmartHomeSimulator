import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories

plugins {
    kotlin("jvm") version "2.1.10"
}

repositories {
    mavenCentral()
}

sourceSets {
    main {
        kotlin.srcDirs("src")
    }
}
dependencies {
}