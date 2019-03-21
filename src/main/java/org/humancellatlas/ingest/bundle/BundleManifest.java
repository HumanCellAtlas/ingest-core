package org.humancellatlas.ingest.bundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.hateoas.Identifiable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by rolando on 05/09/2017.
 */
@AllArgsConstructor
@Getter
public class BundleManifest implements Identifiable<String> {
    private @Id @JsonIgnore String id;

    private final String bundleUuid;
    private final String bundleVersion;
    private final String envelopeUuid;

    private final List<String> dataFiles;
    private final Map<String, Collection<String>> fileBiomaterialMap;
    private final Map<String, Collection<String>> fileProcessMap;
    private final Map<String, Collection<String>> fileProjectMap;
    private final Map<String, Collection<String>> fileProtocolMap;
    private final Map<String, Collection<String>> fileFilesMap;

}
