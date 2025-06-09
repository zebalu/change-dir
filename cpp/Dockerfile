FROM ubuntu:latest

RUN apt update
RUN apt install -y gcc g++
RUN apt install -y openjdk-21-jdk

WORKDIR /cpp

CMD ./gradlew jchdir_jniSharedLibrary

