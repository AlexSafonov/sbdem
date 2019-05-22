FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/sbdem-0.0.1-SNAPSHOT.jar sbdem-0.0.1-SNAPSHOT.jar
CMD java -Xmx512M -Xms512M -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar /sbdem-0.0.1-SNAPSHOT.jar



