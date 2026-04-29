# -------- BUILD STAGE --------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# -------- RUN STAGE --------
FROM eclipse-temurin:17-jdk

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# 🔥 DO NOT hardcode 8080
EXPOSE 10000

# 🔥 CRITICAL FIX
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-10000} -Dserver.address=0.0.0.0 -jar app.jar"]