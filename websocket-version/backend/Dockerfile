# Use Java 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy backend files and jars
COPY . .

# Compile all Java files with external jars
RUN javac -cp ".:gson-2.10.1.jar:Java-WebSocket-1.6.0.jar:slf4j-api-2.0.9.jar:slf4j-simple-2.0.9.jar" *.java

# Expose the port your WebSocket server listens on
EXPOSE 8081

# Run the WebSocket server
CMD ["java", "-cp", ".:gson-2.10.1.jar:Java-WebSocket-1.6.0.jar:slf4j-api-2.0.9.jar:slf4j-simple-2.0.9.jar", "ChatServerWebSocket"]
