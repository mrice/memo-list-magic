FROM java:8

# Expose ports:
# - smtp  : 3025
# - smtps : 3465
# - pop3  : 3110
# - pop3s : 3995
# - imap  : 3143
# - imaps : 3993
EXPOSE 3025 3465 3110 3995 3143 3993 80 22

RUN apt-get update
RUN apt-get install -y supervisor

RUN mkdir -p /var/run/sshd /var/log/supervisor

#Install MongoDB
ENV MONGO_USER=mongodb \
    MONGO_DATA_DIR=/var/lib/mongodb \
    MONGO_LOG_DIR=/var/log/mongodb

#docker run -i -t --entrypoint /bin/bash memo
 
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 7F0CEB10 \
 && echo 'deb http://downloads-distro.mongodb.org/repo/debian-sysvinit dist 10gen' > /etc/apt/sources.list.d/mongodb.list \
 && apt-get update \
 && DEBIAN_FRONTEND=noninteractive apt-get install -y mongodb-org-server mongodb-org-shell \
 && sed 's/^bind_ip/#bind_ip/' -i /etc/mongod.conf \
 && rm -rf /var/lib/apt/lists/*

COPY mongod.conf /etc/mongod.conf
 
COPY mongoDBStartup.sh /sbin/mongoDBStartup.sh
RUN chmod 755 /sbin/mongoDBStartup.sh
 
EXPOSE 27017
VOLUME ["${MONGO_DATA_DIR}"]

# Run as user greenmail
RUN addgroup memolister && useradd -g  memolister -m  memolister
# Note: If using Dockerfile without Maven, you must manually copy the JAR
ADD target/greenmail-standalone.jar /home/memolister/greenmail-standalone.jar
RUN chown memolister:memolister /home/memolister/greenmail-standalone.jar

ADD target/memo-list-magic-standalone.jar /home/memolister/memo-list-magic-standalone.jar
RUN chown memolister:memolister /home/memolister/memo-list-magic-standalone.jar

WORKDIR /home/memolister
# Run GreenMail Standalone with test setup and disabled authentication
#ENTRYPOINT ["java", "-jar", "memo-list-magic-standalone.jar", "ArgOne", "ArgTwo", "ArgThree"]
ENTRYPOINT ["/usr/bin/supervisord"]
#ENTRYPOINT ["/sbin/mongoDBStartup.sh"]

ADD supervisord.conf /etc/supervisor/conf.d/supervisord.conf