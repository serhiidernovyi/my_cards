FROM openjdk:17
EXPOSE 8070
ADD /target/cards-0.1.jar cards.jar
ENTRYPOINT ["java","-jar","cards.jar"]