# Kafka Client Example

Resources:

- <https://docs.confluent.io/platform/current/installation/versions-interoperability.html#supported-versions-and-interoperability-for-cp>
- <https://docs.confluent.io/kafka-clients/java/current/overview.html#ak-java>
- <https://docs.confluent.io/platform/current/security/security_tutorial.html#configure-console-producer-and-consumer>


## Build

Build multiple architectures: <https://cloudolife.com/2022/03/05/Infrastructure-as-Code-IaC/Container/Docker/Docker-buildx-support-multiple-architectures-images/>

```bash
docker buildx build --platform linux/amd64,linux/arm64 -t <image-name> --push .
```