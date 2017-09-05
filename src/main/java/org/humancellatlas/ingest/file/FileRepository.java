package org.humancellatlas.ingest.file;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by rolando on 01/09/2017.
 */
public interface FileRepository extends MongoRepository<File, String> {
}