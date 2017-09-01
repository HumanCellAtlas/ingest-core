package org.humancellatlas.ingest.bundle;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by rolando on 01/09/2017.
 */
@Getter
public class Bundle {
    private String id;
    private List<String> bundleFiles;

    @JsonCreator
    public Bundle(){
        this.id = UUID.randomUUID().toString();
        bundleFiles = new ArrayList<>();
    }
}
