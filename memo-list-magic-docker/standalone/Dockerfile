FROM java:8

# Run as user greenmail
RUN addgroup memolister && useradd -g  memolister -m  memolister

ADD target/memo-list-magic-standalone.jar /home/memolister/memo-list-magic-standalone.jar
RUN chown memolister:memolister /home/memolister/memo-list-magic-standalone.jar

WORKDIR /home/memolister
# Run GreenMail Standalone with test setup and disabled authentication
ENTRYPOINT ["java", "-jar", "memo-list-magic-standalone.jar", "ArgOne", "ArgTwo", "ArgThree"]