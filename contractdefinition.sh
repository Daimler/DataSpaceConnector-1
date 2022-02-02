#!/bin/bash

assetId="$1"

curl -X POST "http://localhost:8181/api/mgmt/contractdefinition/${assetId}"
