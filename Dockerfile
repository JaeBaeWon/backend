FROM eclipse-temurin:17-jre

# JAR 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar /app/backend.jar
COPY build/libs/aws-msk-iam-auth-1.1.7-all.jar /app/aws-msk-iam-auth.jar

# 실행
ENTRYPOINT ["java", "-cp", "/app/aws-msk-iam-auth.jar:/app/backend.jar", "org.example.backend.BackendApplication"]
