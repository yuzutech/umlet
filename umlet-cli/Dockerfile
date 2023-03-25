## UMLet
FROM ubuntu:jammy-20230308 as kroki-builder-umlet-native

ARG GRAALVM_VERSION="22.3.1"

RUN apt update && apt install -y \
      wget \
      build-essential \
      libz-dev \
      zlib1g-dev \
      libfreetype6-dev \
      && rm -rf /var/lib/apt/lists/*

COPY target/umlet.jar fixtures/state.xml ./

RUN wget "https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAALVM_VERSION}/graalvm-ce-java17-linux-amd64-${GRAALVM_VERSION}.tar.gz" -O graalvm-ce.tar.gz \
      && mkdir graalvm-ce \
      && tar xvzf graalvm-ce.tar.gz -C graalvm-ce --strip-components 1 \
      && ./graalvm-ce/bin/gu install native-image \
      && mkdir native-image-config-dir

RUN cat state.xml | ./graalvm-ce/bin/java -Djava.awt.headless=true -agentlib:native-image-agent=config-output-dir=./native-image-config-dir -jar umlet.jar svg \
      && ./graalvm-ce/bin/native-image -H:ConfigurationFileDirectories=./native-image-config-dir --no-fallback --report-unsupported-elements-at-runtime -jar umlet.jar

