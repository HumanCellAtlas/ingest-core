package org.humancellatlas.ingest.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import org.humancellatlas.ingest.core.Accession;
import org.humancellatlas.ingest.core.EntityType;
import org.humancellatlas.ingest.core.MetadataDocument;
import org.humancellatlas.ingest.core.SubmissionDate;
import org.humancellatlas.ingest.core.UpdateDate;
import org.humancellatlas.ingest.core.Uuid;

import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 30/08/17
 */
@Getter
public class Project extends MetadataDocument {
    protected Project() {
        super(EntityType.PROJECT, null, new SubmissionDate(new Date()), new UpdateDate(new Date()), null, null);
    }

    public Project(EntityType type, String uuid, SubmissionDate submissionDate, UpdateDate updateDate, Accession accession, LinkedHashMap content) {
        super(type, uuid, submissionDate, updateDate, accession, content);
    }
}