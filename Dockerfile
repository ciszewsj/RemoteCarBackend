FROM openjdk:17-alpine
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
RUN mkdir /images && chmod 777 /images
COPY src ./src
CMD ["./mvnw", "spring-boot:run"]
