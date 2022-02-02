#!/bin/bash

negotiationId="$1"

curl --location --request GET "http://localhost:8181/api/control/negotiation/${negotiationId}" --header 'X-API-Key: password' | jq
