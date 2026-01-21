# Use Java 17 LTS base image
FROM openjdk:17-jdk-slim

# Set working directory inside the container
WORKDIR /chat-application

# Copy all your Java files into the container
COPY . .

# Compile the Java files
RUN javac *.java

# Run the main class (replace Main with your actual main class name)
CMD ["java", "ChatServer"]

