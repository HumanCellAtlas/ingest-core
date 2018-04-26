package org.humancellatlas.ingest.schemas.web;

import org.humancellatlas.ingest.schemas.Schema;
import org.humancellatlas.ingest.schemas.VersionMap;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Created by rolando on 19/04/2018.
 */
@Component
public class SchemaResourceProcessor implements ResourceProcessor<Resource<Schema>> {

    public Resource<Schema> process(Resource<Schema> resource) {
        resource.add(resource.getContent()
                             .getVersionMaps().stream()
                             .map(this::linkFromVersionMap)
                             .collect(Collectors.toList()));

        return resource;
    }

    private Link linkFromVersionMap(VersionMap versionMap) {
        return new Link(versionMap.getSchemaUri(), versionMap.getSchemaVersion() + "-schema");
    }
}
