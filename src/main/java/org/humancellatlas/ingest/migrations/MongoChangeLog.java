package org.humancellatlas.ingest.migrations;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ServerVersion;

@ChangeLog
public class MongoChangeLog {
    @ChangeSet(order = "2019-10-29", id="incrementFeatureCompatibilityVersion", author = "alexie.staffer@ebi.ac.uk", runAlways = true)
    public void incrementFeatureCompatibilityVersion(MongoDatabase db) {
        // This migration will (if required) increment the feature compatibility version of the database to match the server version.
        ServerVersion featureCompatibilityVersion = MongoVersionHelper.getFeatureCompatibilityVersion(db);
        ServerVersion serverVersion = MongoVersionHelper.getServerVersion(db);
        if (MongoVersionHelper.canIncrementFeatureCompatibility(featureCompatibilityVersion, serverVersion)) {
            MongoVersionHelper.incrementFeatureCompatibility(db, serverVersion);
        }
    }
}
