# Start with a base image containing Java runtime for the build stage
FROM ibm-semeru-runtimes:open-21.0.1_12-jdk-jammy as build

# Add Maintainer Info
LABEL maintainer="your_email@example.com"

# Set the working directory in the Docker image
WORKDIR /app

# Copy maven executable to the image
COPY mvnw .
COPY .mvn .mvn

# Give execution rights to the Maven wrapper
RUN chmod +x mvnw

# Copy the pom.xml file
COPY pom.xml .

# Copy the project source
COPY src src

# Package the application
RUN ./mvnw package -DskipTests

# Use the same Java version for the runtime stage
FROM ibm-semeru-runtimes:open-21.0.1_12-jre-jammy
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
