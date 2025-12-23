FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy Maven settings
RUN mkdir -p /root/.m2
COPY maven/settings.xml /root/.m2/settings.xml

# Build
COPY . .
RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
CMD ["java","-jar","/app/app.jar"]
