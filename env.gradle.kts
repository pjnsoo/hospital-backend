import java.nio.charset.StandardCharsets
import java.util.Date

// 1. 파일 경로 설정
val envFile = file("$rootDir/.env")
val logYml = file("$rootDir/src/main/resources/log.yml")

// 2. env.local 파일이 없으면 자동 생성 (UTF-8)
if (!envFile.exists()) {
    val today = Date().toString()
    envFile.writeText("""
        # 🔒 환경변수 설정 파일
        # 생성일: $today
        # 이 파일은 Git에 올라가지 않으며, 앱 실행 시 자동으로 주입됩니다.
        # 선언된 별수는 application.yml의 ${'$'}{변수} 에 들어갈 값입니다.
        
        # 절대 민감한 정보들은 yml에 직접 설정하지마세요
        # 저장소 유출 시, 위험합니다.
        
        # 서버마다 다를수 있기 때문에 환경변수로 설정
        SERVER.PORT=8080

        # [Database Configuration]
        DB.URL=jdbc:mariadb://localhost:3306/hospital_db
        DB.USERNAME=username
        DB.PASSWORD=password
        
        # [Encryption]
        JASYPT.KEY=JASYPT키를설정하세요
        
        # [Security]
        JWT.SECRET=시크릿키설정하세요
        JWT.ISSUER=보통도메인으로설정합니다
    """.trimIndent(), StandardCharsets.UTF_8)
    println("✅ [Gradle] '${envFile.name}' 파일이 생성되었습니다.")
}

if (!logYml.exists()) {
    val today = Date().toString()
    logYml.writeText("""
        logging:
          level:
            root: info
    """.trimIndent(), StandardCharsets.UTF_8)
    println("✅ [Gradle] '${logYml.name}' 파일이 생성되었습니다.")
}