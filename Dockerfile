FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1 AS build

COPY ./ ./

RUN sbt compile clean package


FROM openjdk:8-jre-alpine3.9

COPY --from=build ./target/scala-2.13/oilparserapi_2.13-0.1.0-SNAPSHOT.jar /oil-parser-api.jar

ENTRYPOINT ["java", "-cp", "oil-parser-api.jar", "Server"]