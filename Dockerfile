FROM eclipse-temurin:17-jre

# JAR 복사
COPY build/libs/backend-0.0.1-SNAPSHOT.jar /app/backend.jar

# AWS MSK IAM Auth는 자동으로 Spring Kafka에서 classpath에 넣음 → 따로 안 해도 됨
# ENTRYPOINT에서는 -cp 제거하고 그냥 -jar 사용
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
