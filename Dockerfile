# -------- BUILD STAGE --------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# -------- RUN STAGE --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/medical-intern-system-0.0.1-SNAPSHOT.jar app.jar

# optional (not required but safe)
EXPOSE 8080

# 🔥 DO NOT FIX PORT HERE
ENTRYPOINT ["java", "-Xmx256m", "-jar", "app.jar"]