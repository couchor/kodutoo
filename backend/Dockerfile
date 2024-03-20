FROM openjdk:21
# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY /target/kodutoo-0.0.1.jar app/kodutoo.jar

# Expose the port your application runs on
EXPOSE 8085

# Run the jar file
ENTRYPOINT ["java", "-jar", "app/kodutoo.jar"]