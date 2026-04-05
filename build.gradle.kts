plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.allopen") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.21"
    kotlin("plugin.jpa") version "2.2.21"
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project
val kotlinxSerializationJson: String by project
val webBundlerVersion = "1.9.3"
val langchainMarkdownVersion = "1.8.0-beta15"
val mutinyVersion = "2.0.0"
val otelExtension = "1.59.0"
val qdrantLangchainVersion = "1.12.2-beta22"

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-camel-bom:${quarkusPlatformVersion}"))
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:quarkus-langchain4j-bom:${quarkusPlatformVersion}"))

    // Base
    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-arc")
    // Web API
    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-rest-qute")
    // Static resources
    implementation("io.quarkiverse.web-bundler:quarkus-web-bundler:${webBundlerVersion}")
    // AI
    implementation("org.apache.camel.quarkus:camel-quarkus-langchain4j-chat")
    implementation("org.apache.camel.quarkus:camel-quarkus-langchain4j-tools")
    implementation("org.apache.camel.quarkus:camel-quarkus-langchain4j-agent")
    implementation("org.apache.camel.quarkus:camel-quarkus-langchain4j-web-search")
    implementation("dev.langchain4j:langchain4j-ollama")
    implementation("dev.langchain4j:langchain4j-anthropic")
    implementation("dev.langchain4j:langchain4j-qdrant:$qdrantLangchainVersion")
    implementation("dev.langchain4j:langchain4j-document-parser-markdown:$langchainMarkdownVersion")
    implementation("io.quarkiverse.langchain4j:quarkus-langchain4j-skills")
    // Observability
    implementation("io.quarkus:quarkus-opentelemetry")
    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinxSerializationJson}")
    implementation("io.smallrye.reactive:mutiny-kotlin:${mutinyVersion}")

    testImplementation("io.quarkus:quarkus-junit5")
}

group = "me.davidgomesdev.mindcura"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
        javaParameters = true
    }
}

tasks.withType<Test> {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    jvmArgs = listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED")
}

