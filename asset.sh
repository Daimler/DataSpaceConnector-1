#!/bin/bash

id="$1"
data="$2"

curl -X POST "http://localhost:8181/api/mgmt/asset/${id}/${data}"
