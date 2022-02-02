#!/bin/bash

curl --location -X GET "http://localhost:8181/api/control/catalog?provider=http://localhost:8181/api/ids/multipart" --header "X-API-Key: password" | jq
