import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

group = "tikione"
version = "1.7.0"

plugins {
    val kotlinVersion = "1.2.60"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
}

apply {
    plugin("kotlin")
    //plugin("idea")
    plugin("com.github.ksoichiro.build.info")
    plugin("com.github.ben-manes.versions")
}

buildscript {
    repositories {
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin")
        classpath("com.github.ksoichiro:gradle-build-info-plugin:0.2.0")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.20.0")
    }
}

repositories {
    mavenCentral()
    jcenter()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

tasks {
    "dependencyUpdates"(DependencyUpdatesTask::class) {
        resolutionStrategy {
            componentSelection {
                all {
                    val rejected = listOf("alpha", "Alpha", "ALPHA", "b", "beta", "Beta", "BETA", "rc", "?RC", "M", "EA", "pr")
                            .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-]*") }
                            .any { it.matches(candidate.version) }
                    if (rejected) {
                        reject("snapshot")
                    }
                }
            }
        }
    }
    "jar" {
        doFirst {
            File("$buildDir/resources/main/version.txt").writeText(project.version.toString())
            "manifest" {
                if (!configurations.compile.isEmpty) {
                }
            }
            /*manifest {
                if (!configurations.compile.isEmpty()) {
                    attributes(
                            "Main-Class" = "fr.tikione.c2e.Main",
                            "Class-Path" =  configurations.runtimeClasspath.files.collect { "lib/" + it.getName() }.join(" ")
                    )
                }
            }*/
        }
    }
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compile("net.sf.jmimemagic:jmimemagic:0.1.5")
    compile("commons-io:commons-io:2.6")
    compile("commons-codec:commons-codec:1.11")
    compile("org.jsoup:jsoup:1.11.3")
    compile("org.slf4j:slf4j-api:1.7.25")
    compile("ch.qos.logback:logback-classic:1.2.3")
    compile("org.codehaus.janino:janino:3.0.8")
    compile("com.github.salomonbrys.kodein:kodein:4.1.0")
    testCompile("junit:junit:4.12")
    testCompile("org.jetbrains.kotlin:kotlin-test-junit")
}
