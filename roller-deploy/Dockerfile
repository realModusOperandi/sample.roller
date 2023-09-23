FROM icr.io/appcafe/ibm-semeru-runtimes:open-11-jdk-ubi as builder

USER root
COPY . build
RUN cd /build/roller/ && ./gradlew ear
RUN cd /build/roller-ui && ./gradlew war

FROM icr.io/appcafe/websphere-traditional

RUN mkdir -p /opt/IBM/WebSphere/libs/ && chown 1001:0 /opt/IBM/WebSphere/libs/
COPY --chown=1001:0 --from=builder build/roller/roller-lib/build/libs/roller-lib.jar /opt/IBM/WebSphere/libs/roller-lib.jar
COPY --chown=1001:0 roller-deploy/sharedlib.py /work/config/

RUN /work/configure.sh