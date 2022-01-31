#!/bin/bash

port="$1"
id="$2"
data="$3"

curl -X POST "http://localhost:${port}/api/mgmt/asset/${id}/${data}"
