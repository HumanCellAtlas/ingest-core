package org.humancellatlas.ingest.core;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 31/08/17
 */
@Getter
@Setter
public abstract class MetadataDocument extends AbstractEntity {
    private Accession accession;
    private LinkedHashMap<String, Object> content;

    protected MetadataDocument(EntityType type,
                               String uuid,
                               SubmissionDate submissionDate,
                               UpdateDate updateDate,
                               Accession accession,
                               LinkedHashMap content) {
        super(type, uuid, submissionDate, updateDate);

        this.accession = accession;
        this.content = content;
    }

    protected MetadataDocument(){

    }

}
