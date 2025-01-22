FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/data-staging /data-staging
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"] 