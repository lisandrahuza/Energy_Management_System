# Stage 1: Build the application
FROM maven:3.8.3-openjdk-17 AS builder
COPY ./src/ /root/src
COPY ./pom.xml /root/
COPY ./checkstyle.xml /root/
WORKDIR /root
RUN mvn package

# Stage 2: Run the application
FROM openjdk:17-jdk-slim-buster
ENV TZ=UTC
ENV DB_IP=db_chat
ENV DB_PORT=5432
ENV DB_USER=root
# Handle sensitive data securely
# Consider using Docker secrets or passing at runtime instead of hardcoding
ENV DB_PASSWORD=postgres
ENV DB_DBNAME=chatdb

# Copy the built JAR file and necessary dependencies
COPY --from=builder /root/target/ds-2020-0.0.1-SNAPSHOT.jar /root/app.jar

# Optional: List files for debugging (remove in production)
RUN ls -l /root

# Entry point to run the Spring Boot application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:+UnlockExperimentalVMOptions", "-XX:MaxRAMFraction=1", "-Xms512m", "-Xmx512m", "-XX:+UseG1GC", "-Xss512k", "-XX:MaxRAM=72m", "-jar", "/root/app.jar"]
