# 빌드 스테이지
FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Maven 빌드를 실행하고 결과를 확인합니다.
RUN mvn -B package -DskipTests

# 실행 스테이지
FROM openjdk:17
WORKDIR /app
# 빌드 스테이지에서 생성된 JAR 파일을 복사합니다.
COPY --from=build /app/target/*.jar app.jar
EXPOSE 80
CMD ["java", "-jar", "app.jar"]
