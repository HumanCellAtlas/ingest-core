package org.humancellatlas.ingest.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.hateoas.Identifiable;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 30/08/17
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class AbstractEntity implements Identifiable<String> {
    private @Id @JsonIgnore String id;

    private @JsonIgnore EntityType type;

    protected String uuid;
    protected SubmissionDate submissionDate;
    protected UpdateDate updateDate;

    protected AbstractEntity(EntityType type,
                             String uuid,
                             SubmissionDate submissionDate,
                             UpdateDate updateDate) {
        this.type = type;
        this.uuid = uuid;
        this.submissionDate = submissionDate;
        this.updateDate = updateDate;
    }

    protected AbstractEntity(){

    }
}
