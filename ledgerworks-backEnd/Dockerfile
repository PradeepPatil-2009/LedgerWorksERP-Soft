# ===== Stage 1: build the jar =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies first (pom rarely changes relative to sources).
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# Build the application jar (tests run in CI, not in the image build).
COPY src ./src
RUN mvn -q -B -DskipTests package

# ===== Stage 2: slim runtime =====
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/ledgerworks-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
