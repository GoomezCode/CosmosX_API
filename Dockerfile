FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/CosmosX-2.0.2.jar app.jar
EXPOSE 8080
CMD ["sh", "-c", "java -Xms256m -Xmx256m -XX:MaxMetaspaceSize=160m -jar /app/app.jar --server.port=$PORT"]