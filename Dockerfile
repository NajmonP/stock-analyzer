FROM amazoncorretto:26 AS build

WORKDIR /stock-analyzer

RUN yum install -y tar gzip && yum clean all

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests


FROM amazoncorretto:26

WORKDIR /stock-analyzer

COPY --from=build /stock-analyzer/target/*.jar stock-analyzer.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "stock-analyzer.jar"]