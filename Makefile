HUB_ORG=humancellatlas
IMAGE=ingest-core:4d2475b
REPO=bhannafi-f2f-demo

all: image

image:
	docker build -t $(IMAGE) .

push:
	./push_image.sh $(IMAGE) $(REPO)

.PHONY: image publish

