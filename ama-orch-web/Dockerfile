FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Render environment settings for YouTube API access
ENV JAVA_OPTS="-Dhttp.maxRedirects=5 -Dhttps.maxRedirects=5 -Dcom.sun.jndi.ldap.connect.pool=false -Duser.timezone=UTC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
