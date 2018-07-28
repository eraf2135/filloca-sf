FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/filloca-sf.jar /filloca-sf/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/filloca-sf/app.jar"]
