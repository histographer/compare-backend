
FROM maven:3.5.2-jdk-8-alpine AS MAVEN_TOOL_CHAIN
WORKDIR /tmp/
COPY pom.xml /tmp/
RUN mvn -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline
COPY src /tmp/src/
RUN mvn -s /usr/share/maven/ref/settings-docker.xml -Dskip.unit.tests=true package

FROM tomcat
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/*.war /usr/local/tomcat/webapps/ROOT.war

