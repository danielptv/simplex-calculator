plugins {
    java
    checkstyle
}

group = "com.danielptv"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.javaVersion.get()))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    configFile = file("config/checkstyle/checkstyle.xml")
    setConfigProperties(
        "configDir" to "$projectDir/config/checkstyle",
    )
    isIgnoreFailures = false
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Main-Class" to "com.danielptv.simplex.presentation.Application"
            )
        )
    }
}
