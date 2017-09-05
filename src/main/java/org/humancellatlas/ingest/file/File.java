package org.humancellatlas.ingest.file;

import lombok.Getter;
import org.humancellatlas.ingest.core.AbstractEntity;
import org.humancellatlas.ingest.core.EntityType;
import org.humancellatlas.ingest.core.SubmissionDate;
import org.humancellatlas.ingest.core.UpdateDate;


/**
 * Created by rolando on 01/09/2017.
 */
@Getter
public class File extends AbstractEntity {
    private String fileName;
    private String cloudUrl;

    protected File(){

    }

    protected File(EntityType type, String uuid, SubmissionDate submissionDate, UpdateDate updateDate) {
        super(type, uuid, submissionDate, updateDate);
    }
}
