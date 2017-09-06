package org.humancellatlas.ingest.project;

import org.humancellatlas.ingest.core.Uuid;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 31/08/17
 */
public interface ProjectRepository extends MongoRepository<Project, String> {
    public Project findByUuid(Uuid uuid);
}