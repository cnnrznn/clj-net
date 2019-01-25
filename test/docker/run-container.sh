#!/bin/bash

N=$1

docker run \
    --net mynet \
    --ip "100.10.10.1$N" \
    -a stdout -a stderr -a stdin \
    bb \
    lein run $N "hello, world!"
