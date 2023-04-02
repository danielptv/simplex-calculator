plugins {
    java
    checkstyle
}

group = "com.danielptv"
version = "1.0"

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

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    configFile = file("config/checkstyle/checkstyle.xml")
    setConfigProperties(
        "configDir" to "$projectDir/config/checkstyle",
    )
    isIgnoreFailures = false
}
