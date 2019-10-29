package org.humancellatlas.ingest.migrations;

import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ServerVersion;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

class MongoHelper {
    private static ServerVersion getServerVersionFromString(String version) {
        List<Integer> numberList = new ArrayList<>();
        for(String number : version.split("\\.")) {
            numberList.add(Integer.parseInt(number));
        }
        while (numberList.size() < 3) {
            numberList.add(0);
        }
        return new ServerVersion(numberList);
    }
    private static ServerVersion getMajorMinor(ServerVersion version) {
        List<Integer> list = new ArrayList<>();
        list.add(version.getVersionList().get(0));
        list.add(version.getVersionList().get(1));
        list.add(0);
        return new ServerVersion(list);
    }

    private static String getMajorMinorString(ServerVersion version) {
        String major = version.getVersionList().get(0).toString();
        String minor = version.getVersionList().get(1).toString();
        return major + "." + minor;
    }

    static ServerVersion getFeatureCompatibilityVersion(MongoDatabase db) {
        String key = "featureCompatibilityVersion";
        Document compatibility_doc = db.runCommand(new Document("getParameter", 1).append(key, 1));
        if (compatibility_doc.containsKey("ok") && compatibility_doc.containsKey(key)) {
            return getServerVersionFromString(compatibility_doc.getString(key));
        }
        throw new UnsupportedOperationException("Could not retrieve featureCompatibilityVersion.");
    }

    static ServerVersion getServerVersion(MongoDatabase db) {
        Document server_doc = db.runCommand(new Document("buildinfo", 1));
        if (server_doc.containsKey("ok") && server_doc.containsKey("version")) {
            return getServerVersionFromString(server_doc.getString("version"));
        }
        throw new UnsupportedOperationException("Could not retrieve server version.");
    }

    static Boolean canIncrementFeatureCompatibility(ServerVersion featureCompatibility, ServerVersion serverVersion) {
        //Only use Major and Minor Version for compatibility check.
        ServerVersion compatibility = getMajorMinor(featureCompatibility);
        ServerVersion server = getMajorMinor(serverVersion);
        return (compatibility.compareTo(server) < 0);
    }

    static void incrementFeatureCompatibility(MongoDatabase db, ServerVersion serverVersion) {
        db.runCommand( new Document("setFeatureCompatibilityVersion", MongoHelper.getMajorMinorString(serverVersion)));
    }
}
