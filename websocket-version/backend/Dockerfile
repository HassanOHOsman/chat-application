# Use Java 17
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy backend files and jars
COPY . .

# Compile all Java files recursively
RUN javac -cp ".:*.jar" $(find . -name "*.java")

# Expose the port your WebSocket server listens on
EXPOSE 8081

# Run the WebSocket server
CMD ["java", "-cp", ".:*.jar", "ChatServerWebSocket"]
