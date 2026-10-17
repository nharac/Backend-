
# Etapa 1: Construcción (Build)
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos el pom.xml primero para aprovechar la caché de Docker en las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código y compilamos
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Run)
FROM eclipse-temurin:17-jre

WORKDIR /app

# Zona horaria de Colombia para que las fechas se guarden correctamente
ENV TZ=America/Bogota

# Copiamos el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto que expone Spring Boot
EXPOSE 8080

# Comando para arrancar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]