FROM eclipse-temurin:25-jdk
WORKDIR /app
COPY . .
RUN ./gradlew build -x test --no-daemon
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
