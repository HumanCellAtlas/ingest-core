package org.humancellatlas.ingest.file;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by rolando on 01/09/2017.
 */
@Getter
public class File {
    private String id;
    private String metadataUUID;

    @JsonCreator
    public File(){
        this.id = UUID.randomUUID().toString();
        metadataUUID = "";
    }
}
