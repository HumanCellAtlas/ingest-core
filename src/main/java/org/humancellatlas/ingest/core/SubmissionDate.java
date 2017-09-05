package org.humancellatlas.ingest.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 30/08/17
 */
@Data
public class SubmissionDate {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date date;

    protected SubmissionDate() {
        this.date = null;
    }

    public SubmissionDate(Date date) {
        if (!isValid(date)) {
            throw new IllegalArgumentException(String.format("Submission date '%s' is in the future!", date));
        }
        this.date = date;
    }

    public static boolean isValid(Date date) {
        return !date.after(new Date());
    }
}
