FROM alpine:latest
RUN apk add --no-cache coreutils curl
COPY ./configs /configs
COPY ./scripts /scripts
CMD [ "sh", "/scripts/register_connectors.sh" ]
