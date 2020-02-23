
FROM maven:3.5.2-jdk-8-alpine AS MAVEN_TOOL_CHAIN
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package

FROM tomcat
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/*.war /usr/local/tomcat/webapps/ROOT.war

