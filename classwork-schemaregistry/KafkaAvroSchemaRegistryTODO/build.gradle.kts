plugins {
    java
    id("com.github.davidmc24.gradle.plugin.avro") version "1.8.0"
}

repositories {
    maven("https://packages.confluent.io/maven/")
}

dependencies {
    implementation("org.apache.avro:avro:1.11.3")
    implementation("org.apache.kafka:kafka-clients")
    implementation("io.confluent:kafka-avro-serializer:7.5.1") {
        exclude(group = "org.apache.commons", module = "commons-compress")
    }
}

val generateAvro = tasks.register<com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask>("generateAvro") {
    source("/src/main/avro/")
    this.setOutputDir(file("/build/generated-main-avro-java/"))
}

tasks.withType<JavaCompile> {
    source(generateAvro)
}
