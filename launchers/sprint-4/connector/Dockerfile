#
#  Copyright (c) 2021 Daimler TSS GmbH
#
#  This program and the accompanying materials are made available under the
#  terms of the Apache License, Version 2.0 which is available at
#  https://www.apache.org/licenses/LICENSE-2.0
#
#  SPDX-License-Identifier: Apache-2.0
#
#  Contributors:
#       Daimler TSS GmbH - Initial API and Implementation
#
#

FROM openjdk:11-jre-slim

WORKDIR /app
COPY ./build/libs/connector.jar /app

EXPOSE 8181

ENTRYPOINT java \
    -Dweb.http.port="8181"  \
    -Dids.webhook.address="http://localhost:8181"  \
    -Dedc.api.control.auth.apikey.key="X-Api-Key" \
    -Dedc.api.control.auth.apikey.value="password" \
    -Djava.security.edg=file:/dev/.urandom \
    -Dedc.ids.title="Eclipse Dataspace Connector" \
    -Dedc.ids.description="Eclipse Dataspace Connector with IDS extensions" \
    -Dedc.ids.maintainer="https://example.maintainer.com" \
    -Dedc.ids.curator="https://example.curator.com" \
    -jar connector.jar
