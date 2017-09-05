package org.humancellatlas.ingest.bundle;

import lombok.Getter;
import org.humancellatlas.ingest.core.AbstractEntity;
import org.humancellatlas.ingest.core.EntityType;
import org.humancellatlas.ingest.core.SubmissionDate;
import org.humancellatlas.ingest.core.UpdateDate;
import org.humancellatlas.ingest.file.File;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

/**
 * Created by rolando on 05/09/2017.
 */
@Getter
public class Bundle extends AbstractEntity {
    @DBRef
    private List<File> files;

    protected Bundle(EntityType type, String uuid, SubmissionDate submissionDate, UpdateDate updateDate) {
        super(type, uuid, submissionDate, updateDate);
    }

    protected Bundle() {
    }
}
