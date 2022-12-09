plugins {
    id("java")
    id("io.freefair.lombok") version "6.5.1"
    checkstyle
}

group = "com.danielptv"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.javatuples:javatuples:1.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

checkstyle {
    toolVersion = "10.0"
    // https://docs.gradle.org/current/dsl/org.gradle.api.plugins.quality.CheckstyleExtension.html
    configFile = file("config/checkstyle/checkstyle.xml")
    setConfigProperties(
        "configDir" to "$projectDir/config/checkstyle",
    )
    isIgnoreFailures = false
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
