# ================================
# Build stage
# ================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copia los archivos de configuración de Gradle
COPY gradle gradle
COPY gradlew build.gradle settings.gradle ./

# Descarga dependencias (aprovecha la caché de Docker)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copia el código fuente
COPY src src

# Construye la aplicación (omitiendo tests para builds más rápidos en prod)
RUN ./gradlew bootJar --no-daemon -x test

# ================================
# Runtime stage
# ================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Crear un usuario no-root por seguridad (recomendado)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia el JAR construido
COPY --from=builder /app/build/libs/*.jar app.jar

# Configuración explícita para Render
# Render define la variable PORT automáticamente.
# Usamos 'sh -c' para permitir la expansión de la variable ${PORT} al arrancar.
ENV PORT=8080
EXPOSE ${PORT}

# Health check (ajustado para usar la variable de entorno)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1

# ENTRYPOINT DINÁMICO
# Esto asigna el puerto de Render a la propiedad server.port de Spring
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]