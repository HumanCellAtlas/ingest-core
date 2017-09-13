package org.humancellatlas.ingest.file;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.humancellatlas.ingest.core.Accession;
import org.humancellatlas.ingest.core.Checksums;
import org.humancellatlas.ingest.core.EntityType;
import org.humancellatlas.ingest.core.Event;
import org.humancellatlas.ingest.core.MetadataDocument;
import org.humancellatlas.ingest.core.SubmissionDate;
import org.humancellatlas.ingest.core.UpdateDate;
import org.humancellatlas.ingest.core.Uuid;
import org.humancellatlas.ingest.state.ValidationState;
import org.humancellatlas.ingest.submission.SubmissionEnvelope;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class File extends MetadataDocument {
    private final String fileName;

    private String cloudUrl;
    private Checksums checksums;

    protected File() {
        super(EntityType.FILE, new Uuid(),
              new SubmissionDate(new Date()),
              new UpdateDate(new Date()),
              new ArrayList<>(), new Accession(),
              ValidationState.DRAFT,
              null,
              null
        );
        this.cloudUrl = "";
        this.fileName = "";
        this.checksums = null;
    }

    protected File(EntityType type,
                   Uuid uuid,
                   SubmissionDate submissionDate,
                   UpdateDate updateDate,
                   List<Event> events,
                   Accession accession,
                   ValidationState validationState,
                   String fileName,
                   String cloudUrl,
                   Checksums checksums,
                   SubmissionEnvelope submissionEnvelope,
                   Object content) {
        super(type, uuid, submissionDate, updateDate, events, accession, validationState, submissionEnvelope, content);
        this.fileName = fileName;
        this.cloudUrl = cloudUrl;
        this.checksums = checksums;
    }

    @JsonCreator
    protected File(@JsonProperty("fileName") String fileName,
                   @JsonProperty("content") Object content) {
        this(EntityType.FILE, new Uuid(),
             new SubmissionDate(new Date()),
             new UpdateDate(new Date()),
             new ArrayList<>(), new Accession(),
             ValidationState.DRAFT,
             fileName,
             "",
             null,
             null,
             content);
    }

    public File addToSubmissionEnvelope(SubmissionEnvelope submissionEnvelope) {
        super.addToSubmissionEnvelope(submissionEnvelope);

        return this;
    }
}
