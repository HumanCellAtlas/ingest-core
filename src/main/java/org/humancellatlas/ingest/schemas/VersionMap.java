package org.humancellatlas.ingest.schemas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by rolando on 26/04/2018.
 */

@AllArgsConstructor
@Getter
public class VersionMap {
    private final String schemaVersion;
    @JsonIgnore
    private final String schemaUri;
}
