FROM ubuntu:xenial

RUN apt-get update
RUN apt-get install -y git wget openjdk-8-jre

RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
RUN chmod +x lein
RUN mv lein /usr/bin
