Memo List Magic Standalone Docker Image
=========

Builds a docker image running current integration environment.

How to build via docker CLI
------------

1. Build image  
   `docker build -t memolist .`

2. Test  
   `docker run -t -i -p 3025:3025 -p 3110:3110 -p 3143:3143 -p 3465:3465 -p 3993:3993 -p 3995:3995 -p 27017:27017 memolist`  
   `telnet ``docker-machine ip`` 3025`
 
Other useful commands for managing docker images

List container information
* docker ps

List more info about a particular container
* docker inspect <container name>

Stop a running container
* docker kill <container name>

Stop all running containers
* docker kill $(docker ps -q -a)

Remove all containers
* docker rm $(docker ps -q -a)

Remove all images
* docker rmi $(docker images -q)

Run a container and inspect and control internals. Useful for debugging an image being created.
* docker run -i -t --entrypoint /bin/bash <name of image>


How to build via Maven
----------------------

Maven uses the excellent [maven docker plugin](https://github.com/rhuss/docker-maven-plugin/).

A quickstart when using Maven from this module:

1. Building  
   `mvn clean docker:build`

2. Running  
   `mvn docker:start`

3. Stopping  
   `mvn docker:stop`

4. Pushing to Docker Hub  
   `mvn docker:push`

Note: For running from Memo List Magic top level Maven root, use the docker profile which is not active by default:  
`mvn clean install -Pdocker`

