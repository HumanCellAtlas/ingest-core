package org.humancellatlas.ingest.sample;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.humancellatlas.ingest.core.*;

import java.util.Date;
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

    @JsonCreator
    protected Sample(LinkedHashMap<String, Object> content){
        super(EntityType.SAMPLE, null, new SubmissionDate(new Date()), new UpdateDate(new Date()), null, content);
    }
}
