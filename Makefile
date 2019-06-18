HUB_ORG=humancellatlas
IMAGE=ingest-core:4d2475b
REPO=bhannafi-f2f-demo

all: image

image:
	docker build -t $(IMAGE) .

push:
	# aws ecr get-login --region us-east-1 --no-include-email
	eval $(aws ecr get-login | sed 's|https://||')
	docker tag $(IMAGE) 861229788715.dkr.ecr.us-east-1.amazonaws.com/${REPO}
	docker push 861229788715.dkr.ecr.us-east-1.amazonaws.com/${REPO}

.PHONY: image publish

