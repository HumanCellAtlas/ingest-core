#!/bin/bash
IMAGE=$1
REPO=$2
eval $(aws ecr get-login --no-include-email --region us-east-1)
docker tag ${IMAGE} 861229788715.dkr.ecr.us-east-1.amazonaws.com/${REPO}
docker push 861229788715.dkr.ecr.us-east-1.amazonaws.com/${REPO}
