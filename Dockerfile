# -------- BUILD STAGE --------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests


# -------- RUN STAGE --------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

# 🔥 VERY IMPORTANT
EXPOSE 10000

# 🔥 THIS FIXES YOUR ISSUE
ENTRYPOINT ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]