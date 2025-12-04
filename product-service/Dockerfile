# ================================
# 1) Build Stage
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom first (better cache)
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn -q -e -B dependency:go-offline

# Copy source code
COPY src ./src

# Build Spring Boot app
RUN mvn -q -e -B clean package -DskipTests


# ================================
# 2) Run Stage
# ================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# JVM options (feel free to edit)
ENV JAVA_OPTS="-Xms128m -Xmx256m"

# Expose port
EXPOSE 8080

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]


