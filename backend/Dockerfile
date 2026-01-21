# Use a valid Java 17 slim base image
FROM eclipse-temurin:17-jdk

# Set working directory inside the container
WORKDIR /backend

# Copy all your Java files
COPY . .

# Compile all Java files recursively
RUN javac $(find . -name "*.java")

# Run the main class
CMD ["java", "ChatServer"]
