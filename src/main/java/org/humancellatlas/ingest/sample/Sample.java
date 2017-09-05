package org.humancellatlas.ingest.sample;

import lombok.Getter;
import org.humancellatlas.ingest.core.*;

import java.util.LinkedHashMap;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 30/08/17
 */
@Getter
public class Sample extends MetadataDocument {
    protected Sample() {
        super(EntityType.SAMPLE, null, null, null, null, null);
    }

    protected Sample(EntityType type, String uuid, SubmissionDate submissionDate, UpdateDate updateDate, Accession accession, LinkedHashMap content) {
        super(type, uuid, submissionDate, updateDate, accession, content);
    }
}
