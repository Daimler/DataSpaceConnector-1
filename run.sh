#!/bin/bash

name="$1"
port="$2"

docker stop "$name"
docker container rm "$name"
docker run --name "$name" --detach -p "$port":8181 --env EDC_IDS_ID="urn:connector:$name" connector
