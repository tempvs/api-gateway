FROM openjdk:25-ea-slim
COPY ./build/libs/*.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8080
CMD exec java -jar tempvs-gateway.jar
