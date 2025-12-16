plugins {
    id("java")
    id("idea")
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
//    kotlin("jvm") version "2.2.20"
}

group = "org.hostpital"
version = "1.0-SNAPSHOT"

idea {
    module {
        excludeDirs.addAll(
            listOf(
                file(".idea"),
                file("gradle"),
                file("logs"),
                file("cert"),
                file("ddl"),
            )
        )
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

//로컬 환경변수 로더 스크립트 불러오기
apply(from = "env.gradle.kts")

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:4.0.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.6")

    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")

    //롬복, 이클립스에서 롬복에 문제가 있는 경우, 버전을 최신화 한다
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // 테스트용 lombok 추가
    testImplementation("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // Jasypt (양방향 암호화 라이브러리)
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")

    implementation("io.github.cdimascio:dotenv-java:3.2.0")


}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Copy>("downloadDependencies") {
    // runtimeClasspath 의존성 모두 복사
    from(configurations.runtimeClasspath)
//    println("${layout.buildDirectory}")
    // 저장할 디렉토리
    into(layout.buildDirectory.dir("dependencies/libs"))
}
