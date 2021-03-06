package org.humancellatlas.ingest.schemas.schemascraper.impl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rolando on 19/04/2018.
 */

public class ListBucketResult {
    public ListBucketResult() {}

    @JacksonXmlProperty(localName = "Name")
    public String name;

    @JacksonXmlProperty(localName = "Contents")
    public List<Content> contents = new ArrayList<>();

    static class Content {
        Content() {}
        @JacksonXmlProperty(localName = "Key")
        public String key;

        public void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}
