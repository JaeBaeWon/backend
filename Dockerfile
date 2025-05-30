FROM eclipse-temurin:17-jre

COPY build/libs/backend-0.0.1-SNAPSHOT.jar /app/app.jar
COPY libs/aws-msk-iam-auth-1.1.7-all.jar /app/aws-msk-iam-auth.jar

ENTRYPOINT ["java", "-cp", "/app/aws-msk-iam-auth.jar:/app/app.jar", "org.example.backend.BackendApplication"]
