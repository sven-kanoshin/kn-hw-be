# Full build dockerfile
FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /app

COPY . /app

RUN chmod -R +x /app
RUN ./gradlew --no-scan --stacktrace -Duser.name="Docker Builder" -ParchiveNameOverride=kn-homework build

FROM bellsoft/liberica-openjdk-alpine:17

COPY --from=builder /app/build/libs/kn-homework.jar /app/

WORKDIR /app

CMD ["java", "-jar", "kn-homework.jar"]