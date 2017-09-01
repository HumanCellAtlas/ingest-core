package org.humancellatlas.ingest.bundle;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by rolando on 01/09/2017.
 */
public interface BundleRepository extends MongoRepository<Bundle, String> {
}
