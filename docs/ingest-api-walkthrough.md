# Ingest API Example Usage Doc

This document describes the process of generating a submission from secondary analysis (green box) to the ingest API (purple). For this interaction we assume green functions like a purple broker. Green should prepare metadata documents, deposit any required files on the staging area and references these files by communicating mappings between analysis metadata documents and their associated file(s). Additionally, green should notify purple the 'input bundle' that was used to generate the results of the analysis.

This API makes use of REST principles, including the HAL standard for expressing hyperlinks. This enables clients to discover the endpoints within this API with which they should interact. It further allows clients to drive these interactions by following named resource _links_ that are returned when new objects are created. This API also makes use of standard HTTP response codes and supports GET, POST, PUT and DELETE operations.

This document walks through how to carry out the interaction of creating a new secondary analysis submission in the purple box step by step.

## 1. Investigate the API

Examine the ingest API.

```bash
# discover how to root collection, which manages all resource types known to this API
>: curl -X GET http://api.ingest.dev.data.humancellatlas.org/
```

This request returns JSON describing the structure of the API. For example, if we use javascript to parse the response, we can acquire the submission url:
```javascript
// will return http://api.ingest.dev.data.humancellatlas.org/submissionEnvelopes
var subs_url = _links.submissionEnvelopes.href
```
## 2. Create token

```bash
>: TOKEN=$(curl -s https://danielvaughan.eu.auth0.com/oauth/token -X POST -H "Content-Type: application/json"  -d @auth0.json | python -c 'import sys, json; print json.load(sys.stdin)["access_token"]')
```

## 3. Create a New Submission Envelope

Evaluate the expression `_links.submissionEnvelopes.href` from the response to the request in step 1. This 'discovers' the link to use to create a submission envelope (and will protect your client for future changes to URL structures). A submission envelope provides a container for all submitted materials and will allocate staging space to upload any required files to (e.g. analysis outputs or workflow descriptors). We want to create a new empty envelope, so we send an empty JSON object.

```bash
# returns the submission envelope, with embedded links to its url {_links.self.href} and a link to use to create assays {_links.processes}
>: curl -X POST -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN"  "http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes" -d {}
```

## 4. Add Analyses to the Submission Envelope

Evaluate the expression `_links.processes.href` from the response to the previous request. This gives us the location to send analyses to. The below code assumes analysis metadata is held in a file called 'analysis_process.json' in the current directory.

```bash
# returns a link with which to associate files to this analysis {_links.add-file-reference.href} 
#         a link with which to associate protocols to {_links.self.protocols}
>: curl -X POST -H "Content-Type: application/json" -d @analysis_process.json http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5b769dc51c022d00079f1d8f/processes
```

## 5. Add Protocols to the Submission Envelope

Evaluate the expression `_links.protocols.href` from the response to the previous "Create submission" request. This gives us the location to send protocols to. The below code assumes protocol metadata is held in a file called 'analysis_protocol.json' in the current directory.

```bash
# returns a link its url {_links.self.href}
>: curl -X POST -H "Content-Type: application/json" -d @analysis_protocol.json http://api.ingest.integration.data.humancellatlas.org/submissionEnvelopes/5b769dc51c022d00079f1d8f/protocols
```

## 6. Link the Analysis protocol to the Analysis Process

```bash
# returns an HTTP status 204 (No Content)
>: curl -X PUT -H "Content-Type: text/uri-list" -d "http://api.ingest.integration.data.humancellatlas.org/protocols/5b769e991c022d00079f1d95" "http://api.ingest.integration.data.humancellatlas.org/processes/5b769e3d1c022d00079f1d92/protocols"
```

## 7 Link input Files to the Analysis Process

Any files in the DCP that were used as an analysis inputs should be linked to the analysis process. Given an input file UUID, this can be performed as follows:

```bash
>: curl -X POST -H "Content-Type: application/json" http://api.ingest.dev.data.humancellatlas.org/processes/{analysis_id}/inputFiles -d '{"inputFileUuid": "<input file UUID>"}'
```

## 8. Add a Bundle Reference to this Analysis

From the previous expression, use `_links.add-input-bundles.href`. This should give you a URL like the one shown in the example below. Submit a JSON snippet that contains the UUIDs of the bundles used in this analysis.

```bash
# point to the bundle UUID - for now, it doesn't matter if this doesn't exist (in future this will fail)
>: curl -X POST -H "Content-Type: application/json" http://api.ingest.integration.data.humancellatlas.org/processes/5b769e3d1c022d00079f1d92/bundleReferences -d '{ "bundleUuids" : ["e177fe37-1b35-4d52-9df3-e854c5d59306"]}'
```

## 9. Add a File Reference to this Analysis

From the previous expression, use `_links.add-file-reference.href`. This should give you a URL like the one shown in the example below. Submit JSON that describes the file you want to reference.

```bash
>: curl -X PUT -H "Content-Type: application/json" http://api.ingest.dev.data.humancellatlas.org/processes/{analysis_id}/fileReference -d '{"fileName": "ERR1630013.fastq.gz", "content": {"lane": 1, "type": "reads", "name": "ERR1630013.fastq.gz", "format": ".fastq.gz"}}'

```

Repeat this step for all the files referenced by the Analysis

## 10. Repeat for Additional analyses

It's possible to upload multiple analysis results in a single submission. The ingest API will figure out how to map them into bundles in the datastore.

## 11. Upload Files to the Staging Area

See [https://github.com/HumanCellAtlas/dcp-cli/blob/master/docs/data_submission_guide.md](https://github.com/HumanCellAtlas/dcp-cli/blob/master/docs/data_submission_guide.md)

## 12. Confirm your submission

In step 2. you will have received a 'submit' link (it's `_links.submit.href`). You can now use this to confirm your submission. It's also possible to reevaluate the submit link using a call to check on the submission envelope

```bash
# returns a submit link {_links.submit.href}
>: curl -X GET http://api.ingest.dev.data.humancellatlas.org/submissionEnvelopes/{sub_id}
```

To do the submission:

```bash
# returns a dummy receipt confirming submission
>: curl -X PUT -H "Content-Type: application/json" http://api.ingest.dev.data.humancellatlas.org/submissionEnvelopes/{sub_id}/submissionEvent
```

# Privacy
This API require limited processing of personal data. For more information, please read our [privacy policy](http://www.ebi.ac.uk/data-protection/privacy-notice/human-cell-atlas-ingest-access-service).
