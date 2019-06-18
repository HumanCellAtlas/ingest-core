HUB_ORG=humancellatlas
IMAGE=ingest-core:4d2475b

all: image

image:
	docker build -t $(IMAGE) .

push:
	docker tag $(IMAGE) $(HUB_ORG)/$(IMAGE)
	docker push $(HUB_ORG)/$(IMAGE)

.PHONY: image publish

