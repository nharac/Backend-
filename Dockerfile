
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

# Memoria de Java ajustada para una instancia pequeña (AWS t3.micro con 1 GB):
#  - heap de 384 MB (los datos de la app; sobra para un curso)
#  - metaspace y caché de código acotados (clases de Spring y código compilado)
#  - pila de 512 KB por hilo y recolector de basura en serie (el más liviano)
# En total el proceso queda en unos 400-500 MB. Se puede cambiar sin reconstruir
# la imagen con la variable JAVA_OPTS en el .env.
ENV JAVA_OPTS="-Xms128m -Xmx384m -XX:MaxMetaspaceSize=192m -XX:ReservedCodeCacheSize=64m -XX:MaxDirectMemorySize=64m -Xss512k -XX:+UseSerialGC"

# Comando para arrancar la aplicación (exec: Java recibe las señales de Docker para apagarse limpio)
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]