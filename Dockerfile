FROM alpine
RUN apk add --no-cache openjdk17
COPY target/Online-store-rest-api-1.jar Online-store-rest-api-1.jar
ENTRYPOINT ["java","-jar","Online-store-rest-api-1.jar"]