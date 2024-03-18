plugins {
    id("java")
}
java {
    setSourceCompatibility(11)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.projectreactor:reactor-core:3.6.4")

    // micrometer should be in classpath, otherwise Hooks.enableAutomaticContextPropagation does nothing
    implementation("io.micrometer:micrometer-tracing:1.2.1")
}