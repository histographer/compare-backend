FROM tomcat
COPY /target/backend-0.0.2-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war