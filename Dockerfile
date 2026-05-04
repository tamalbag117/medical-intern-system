# -------- BUILD STAGE --------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# -------- RUN STAGE --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/medical-intern-system-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000

ENTRYPOINT ["java", "-Xmx256m", "-Dserver.port=10000", "-Dserver.address=0.0.0.0", "-jar", "app.jar"]