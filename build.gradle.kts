

plugins {
    id("java")
    id("application")
}

group = "lab"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.postgresql:postgresql:42.2.24")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.jar {
    manifest {
        attributes(
                "Main-Class" to "lab.Main",
                "Class-Path" to "."
        )
    }
    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })

}



application {
    mainClass.set("lab.Main")
}
