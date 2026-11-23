# ---- Etapa 1: compilar con Maven ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package && cp target/*.jar app.jar

# ---- Etapa 2: imagen liviana solo con el JRE ----
FROM eclipse-temurin:21-jre
RUN useradd --system --create-home app
WORKDIR /app
COPY --from=build /app/app.jar app.jar
USER app
EXPOSE 8086
ENV JAVA_TOOL_OPTIONS="-Xmx256m -XX:+UseSerialGC"
ENTRYPOINT ["java", "-jar", "app.jar"]
