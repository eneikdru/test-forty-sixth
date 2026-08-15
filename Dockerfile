FROM node:20-alpine AS frontend-build
WORKDIR /build/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
COPY --from=frontend-build /build/frontend/dist ./src/main/resources/static
# Execute full Maven build including unit and integration tests (no -DskipTests) to enforce test integrity
RUN mvn clean package

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY --from=build /build/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
