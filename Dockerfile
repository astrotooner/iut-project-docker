# ÉTAPE 1 : Création du JRE
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app
COPY . .
# 1. Compilation
RUN ./gradlew build -x test --no-daemon

# 2. jlink
RUN jlink \
    --add-modules java.base,java.naming,java.logging,java.management,java.security.jgss,java.desktop,java.xml,java.instrument \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /javaruntime

# ÉTAPE 2 : Image finale légère
FROM debian:bookworm-slim
WORKDIR /app

# On importe le résultat de l'étape 1
COPY --from=builder /javaruntime /opt/java/openjdk
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${PATH}:${JAVA_HOME}/bin"

# On importe l'application
COPY --from=builder /app/build/libs/app.jar ./app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
