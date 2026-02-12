# Use a valid Java 17 slim base image
FROM eclipse-temurin:17-jdk

# Set working directory inside the container
WORKDIR /websocket-version/backend

# Copy everthing in backend folder (java + jars files)
COPY . .

# Compile all Java files recursively
RUN javac -cp ".:gson-2.10.1.jar:slf4j-api-2.0.9.jar:slf4j-simple-2.0.9.jar:Java-WebSocket-1.6.0.jar" *.java

# Run the main class
CMD ["java", "-cp", ".:gson-2.10.1.jar:slf4j-api-2.0.9.jar:slf4j-simple-2.0.9.jar:Java-WebSocket-1.6.0.jar", "ChatServer"]
