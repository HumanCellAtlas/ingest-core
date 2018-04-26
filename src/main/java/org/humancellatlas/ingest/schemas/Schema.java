package org.humancellatlas.ingest.schemas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.humancellatlas.ingest.core.AbstractEntity;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rolando on 18/04/2018.
 */
@AllArgsConstructor
@Getter
public class Schema extends AbstractEntity {
    private final String highLevelEntity;
    private final String domainEntity;
    private final String subDomainEntity;
    private final String concreteEntity;
    @Getter(AccessLevel.NONE)
    private final List<VersionMap> versionMaps = new ArrayList<>();

    @JsonIgnore
    public void addVersion(String schemaVersion, String schemaUri) {
        this.versionMaps.add(new VersionMap(schemaVersion, schemaUri));
    }

    @JsonIgnore
    public UUID genererateUuid() {
        return UUID.nameUUIDFromBytes((highLevelEntity + domainEntity + subDomainEntity + concreteEntity).getBytes());
    }

    public List<VersionMap> getVersionMaps() {
        return new CopyOnWriteArrayList<>(versionMaps);
    }
}
