package org.humancellatlas.ingest.file;

import lombok.Getter;
import lombok.Setter;
import org.humancellatlas.ingest.core.*;


/**
 * Created by rolando on 01/09/2017.
 */
@Getter
@Setter
public class File extends AbstractEntity {
    private String fileName;
    private String cloudUrl;
    private Checksums checksums;

    protected File(){

    }

    protected File(EntityType type, String uuid, SubmissionDate submissionDate, UpdateDate updateDate) {
        super(type, uuid, submissionDate, updateDate);
    }
}
