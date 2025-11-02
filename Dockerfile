# Use official OpenJDK image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy source code
COPY src src

# Give execution permission to mvnw
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package -DskipTests

# Force rebuild cache
ARG CACHE_BREAKER=1

# Expose the port (Render will use $PORT)
ENV PORT=8080
EXPOSE $PORT

# Run the jar file
CMD ["java", "-jar", "target/WanderLust-0.0.1-SNAPSHOT.jar"]
