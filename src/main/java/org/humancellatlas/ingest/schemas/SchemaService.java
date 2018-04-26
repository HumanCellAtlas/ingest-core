package org.humancellatlas.ingest.schemas;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.humancellatlas.ingest.core.Uuid;
import org.humancellatlas.ingest.schemas.schemascraper.SchemaScraper;
import org.humancellatlas.ingest.schemas.schemascraper.impl.SchemaScrapeException;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by rolando on 19/04/2018.
 */
@Service
@RequiredArgsConstructor
@Getter
public class SchemaService {
    private final @NonNull SchemaRepository schemaRepository;
    private final @NonNull SchemaScraper schemaScraper;
    private final @NonNull Environment environment;

    private static final int EVERY_24_HOURS = 1000 * 60 * 60 * 24;

    public Page<Schema> querySchemas(String highLevelEntity,
                                     String concreteEntity,
                                     String domainEntity,
                                     String subDomainEntity,
                                     Pageable pageable){
        return schemaRepository.findByHighLevelEntityLikeAndConcreteEntityLikeAndDomainEntityLikeAndSubDomainEntityLike(highLevelEntity,
                                                                                                                        concreteEntity,
                                                                                                                        domainEntity,
                                                                                                                        subDomainEntity,
                                                                                                                        pageable);
    }

    @Scheduled(fixedDelay = EVERY_24_HOURS)
    public void updateSchemasCollection() {
        Set<DistinctSchema> distinctSchemas = new LinkedHashSet<>();

        schemaScraper.getAllSchemaURIs(URI.create(environment.getProperty("SCHEMA_BASE_URI"))).stream()
                     .filter(schemaUri -> ! schemaUri.toString().contains("index.html"))
                     .forEach(schemaUri -> {
                         Schema schema = schemaDescriptionFromSchemaUri(schemaUri);
                         DistinctSchema distinctSchemaDocument = new DistinctSchema(schema);
                         distinctSchemas.add(distinctSchemaDocument);
                         distinctSchemaDocument.getSchema().addVersion(schemaVersionFromSchemaUri(schemaUri),
                                                                       schemaFullUriFromSchemaUri(schemaUri));
                     });

        persistSchemas(distinctSchemas.stream()
                                      .map(DistinctSchema::getSchema)
                                      .collect(Collectors.toList()));
    }

    public Collection<Schema> schemaDescriptionFromSchemaUris(Collection<URI> schemaUris) {
        return schemaUris.stream()
                         .map(this::schemaDescriptionFromSchemaUri)
                         .collect(Collectors.toList());
    }

    private Schema schemaDescriptionFromSchemaUri(URI relativeSchemaUri) {
        String[] splitString = relativeSchemaUri.toString().split("/");
        String schemaFullUri = schemaFullUriFromSchemaUri(relativeSchemaUri);

        Schema schema;
        if(splitString.length == 3) { // then this is a bundle schema
            schema = new Schema(splitString[0], splitString[1], "", "");
        } else if(splitString.length == 4) {
            schema = new Schema(splitString[0], splitString[2], splitString[1], "");
        } else if(splitString.length == 5) {
            schema = new Schema(splitString[0], splitString[3], splitString[1], splitString[2]);
        } else {
            throw new SchemaScrapeException("Couldn't construct a Schema document from URI: " + schemaFullUri);
        }

        // add the version to the schema
        String schemaVersion = schemaVersionFromSchemaUri(relativeSchemaUri);
        schema.addVersion(schemaVersion, schemaFullUri);
        return schema;
    }

    private String schemaFullUriFromSchemaUri(URI relativeSchemaUri) {
        return environment.getProperty("SCHEMA_BASE_URI") + relativeSchemaUri;
    }

    private String schemaVersionFromSchemaUri (URI relativeSchemaUri) {
        String[] splitString = relativeSchemaUri.toString().split("/");
        // the version is always the 2nd last component of the schema URI
        return splitString[splitString.length - 2];
    }

    private void persistSchemas(Collection<Schema> schemas) {
        // generate a uuid from the schema's attributes, excluding the version
        schemas.forEach(schema -> {
            UUID schemaUuid = schema.genererateUuid();
            schema.setUuid(new Uuid(schemaUuid.toString()));

            // delete/update matching schemas
            Collection<Schema> matchingSchemas = schemaRepository.findByUuidEquals(new Uuid(schemaUuid.toString()));
            schemaRepository.delete(matchingSchemas);

            schemaRepository.save(schema);
        });
    }

    /**
     *
     * A wrapper for Schema documents used to define a looser equals()/hashCode()
     * to determine equivalence of Schemas based only on a Schema's high level entity,
     * type, etc. and ignoring the version
     *
     */
    private class DistinctSchema {
        @Getter
        private final Schema schema;

        DistinctSchema(Schema schema) {
            this.schema = schema;
        }

        @Override
        public boolean equals(Object to) {
            if (to == this) return true;
            if (!(to instanceof DistinctSchema)) {
                return false;
            }

            DistinctSchema schema = (DistinctSchema) to;
            return schema.hashCode() == this.hashCode();
        }

        @Override
        public int hashCode(){
            int result = 17;
            result = 31 * result + this.schema.getConcreteEntity().hashCode();
            result = 31 * result + this.schema.getHighLevelEntity().hashCode();
            result = 31 * result + this.schema.getDomainEntity().hashCode();
            result = 31 * result + this.schema.getSubDomainEntity().hashCode();
            return result;
        }
    }
}
