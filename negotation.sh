#!/bin/bash

curl --location --request POST 'http://localhost:8181/api/control/negotiation' \
--header 'X-API-Key: password' \
--header 'Content-Type: application/json' \
--data-raw '{
  "type": "INITIAL",
  "protocol": "ids-multipart",
  "connectorId": "1",
  "connectorAddress": "http://localhost:8181/api/ids/multipart",
  "correlationId": null,
  "contractOffer":
     {
          "id": "dc69aa2d-929c-4c92-8345-50e9e2e071f0:4c11dfdf-2217-4357-ae89-8240f7e5441d",
          "policy": {
            "uid": "95784d73-cc60-41ed-9234-69796b583cf5",
            "permissions": [
              {
                "edctype": "dataspaceconnector:permission",
                "uid": null,
                "target": "asset-1",
                "action": {
                  "type": "USE",
                  "includedIn": null,
                  "constraint": null
                },
                "assignee": null,
                "assigner": null,
                "constraints": [],
                "duties": []
              }
            ],
            "prohibitions": [],
            "obligations": [],
            "extensibleProperties": {},
            "inheritsFrom": null,
            "assigner": null,
            "assignee": null,
            "target": null,
            "@type": {
              "@policytype": "set"
            }
          },
          "asset": {
            "properties": {
              "ids:byteSize": null,
              "asset:prop:id": "asset-1",
              "ids:fileName": null
            }
          },
          "provider": null,
          "consumer": null,
          "offerStart": null,
          "offerEnd": null,
          "contractStart": null,
          "contractEnd": null
        }

  ,
  "asset": "1",
  "provider": "https://provider.com",
  "consumer": "https://consumer.com",
  "offerStart": null,
  "offerEnd": null,
  "contractStart": null,
  "contractEnd": null
}'
