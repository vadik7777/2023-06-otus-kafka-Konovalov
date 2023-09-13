dependencies {
    implementation("org.apache.kafka:kafka-clients")
    implementation("org.apache.kafka:kafka-streams")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    implementation("org.slf4j:slf4j-api")
    implementation("com.google.code.gson:gson")
    implementation("com.github.javafaker:javafaker") { exclude("org.yaml") }
    implementation("org.yaml:snakeyaml")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.slf4j:slf4j-api")
    implementation("ch.qos.logback:logback-classic")
}
