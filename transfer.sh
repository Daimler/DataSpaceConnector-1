#!/bin/bash

curl --location --request POST 'http://localhost:8181/api/control/transfer' \
--header 'X-API-Key: password' \
--header 'Content-Type: application/json' \
--data-raw '
{
  "edctype": "dataspaceconnector:datarequest",
  "id": null,
  "processId": null,
  "connectorAddress": "http://localhost:8181/api/ids/multipart",
  "protocol": "ids-multipart",
  "connectorId": "consumer",
  "assetId": "asset-1",
  "contractId": "dc69aa2d-929c-4c92-8345-50e9e2e071f0:b2a34afa-f683-4884-a873-db3a06d4b798",
  "dataDestination": {
    "properties": {
      "url": "http://localhost:8181/api/fake/data"
    },
    "type": "fake-api"
  },
  "managedResources": true,
  "transferType": {
    "contentType": "application/octet-stream",
    "isFinite": true
  },
  "destinationType": "fake-api"
}'
