package org.humancellatlas.ingest.migrations;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ServerVersion;

@ChangeLog
public class MongoChangeLog {
    @ChangeSet(order = "2019-10-29", id="incrementFeatureCompatibilityVersion", author = "@MightyAx", runAlways = true)
    public void incrementFeatureCompatibilityVersion(MongoDatabase db) {
        // This migration will (if required) increment the feature compatibility version of the database to match the server version.
        ServerVersion featureCompatibilityVersion = MongoHelper.getFeatureCompatibilityVersion(db);
        ServerVersion serverVersion = MongoHelper.getServerVersion(db);
        if (MongoHelper.canIncrementFeatureCompatibility(featureCompatibilityVersion, serverVersion)) {
            MongoHelper.incrementFeatureCompatibility(db, serverVersion);
        }
    }
}
