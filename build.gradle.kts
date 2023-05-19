@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    checkstyle
    alias(libs.plugins.springBoot)
    alias(libs.plugins.spotbugs)
}

group = "com.danielptv"
version = "2.1.0"

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
    implementation(libs.springShell)
    implementation(libs.jline)
    implementation(libs.jfiglet)
    compileOnly(libs.spotbugsAnnotations)

    testImplementation(libs.junitApi)
    testImplementation(libs.junitPlatformSuiteApi)
    testImplementation(libs.assertj)
    testImplementation(libs.springBootTest)
    testRuntimeOnly(libs.junitPlatformSuiteEngine)
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    configFile = file("extras/checkstyle/checkstyle.xml")
    setConfigProperties(
        "configDir" to "$projectDir/extras/checkstyle",
    )
    isIgnoreFailures = false
}

spotbugs {
    toolVersion.set(libs.versions.spotbugs.get())
}
tasks.spotbugsMain {
    reports.create("html") {
        required.set(true)
        outputLocation.set(file("$buildDir/reports/spotbugs.html"))
    }
}

tasks.test {
    useJUnitPlatform {
        includeTags = setOf("integration", "unit")
    }
}
