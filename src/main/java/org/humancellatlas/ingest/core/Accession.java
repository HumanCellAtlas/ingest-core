package org.humancellatlas.ingest.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 30/08/17
 */
@Data
public class Accession {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String number;

    protected Accession() {
        this.number = null;
    }

    public Accession(String number) {
        if (!isValid(number)) {
            throw new IllegalArgumentException(String.format("Accession number '%s' is not a valid format ", number));
        }
        this.number = number;
    }

    public static boolean isValid(String number) {
        return !number.isEmpty(); // todo might want to regex this, maybe this service doesn't care
    }
}
