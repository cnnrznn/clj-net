#!/bin/bash

N=$1
V=$2

docker run \
    --net mynet \
    --ip "100.10.10.1$N" \
    bb \
    lein run $V
