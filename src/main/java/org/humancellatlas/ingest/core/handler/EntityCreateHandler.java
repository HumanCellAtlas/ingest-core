package org.humancellatlas.ingest.core.handler;

import org.humancellatlas.ingest.core.*;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * Created by rolando on 04/09/2017.
 */
@Component
@RepositoryEventHandler
public class EntityCreateHandler {

    @HandleBeforeCreate
    public void handleNewEntity(AbstractEntity entity) {
        entity.setUuid(UUID.randomUUID().toString());
        entity.setSubmissionDate(new SubmissionDate(new Date()));
        entity.setUpdateDate(new UpdateDate(new Date()));
    }

    @HandleBeforeCreate
    public void handleNewMetadata(MetadataDocument document){
        document.setAccession(new Accession(UUID.randomUUID().toString()));
    }
}