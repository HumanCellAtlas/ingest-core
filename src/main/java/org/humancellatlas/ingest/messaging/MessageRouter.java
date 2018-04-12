package org.humancellatlas.ingest.messaging;

import lombok.NoArgsConstructor;
import org.humancellatlas.ingest.core.*;
import org.humancellatlas.ingest.core.web.LinkGenerator;
import org.humancellatlas.ingest.export.ExportData;
import org.humancellatlas.ingest.messaging.model.MetadataDocumentMessage;
import org.humancellatlas.ingest.messaging.model.SubmissionEnvelopeMessage;
import org.humancellatlas.ingest.messaging.model.SubmissionEnvelopeStateUpdateMessage;
import org.humancellatlas.ingest.state.SubmissionState;
import org.humancellatlas.ingest.state.ValidationState;
import org.humancellatlas.ingest.submission.SubmissionEnvelope;
import org.humancellatlas.ingest.submission.SubmissionEnvelopeMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ResourceMappings;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.humancellatlas.ingest.messaging.Constants.Exchanges.ASSAY_EXCHANGE;
import static org.humancellatlas.ingest.messaging.Constants.Routing.ANALYSIS_SUBMITTED;
import static org.humancellatlas.ingest.messaging.Constants.Routing.ASSAY_SUBMITTED;

/**
 * Created by rolando on 09/03/2018.
 */
@Component
@NoArgsConstructor
public class MessageRouter {

    @Autowired private MessageSender messageSender;
    @Autowired private ResourceMappings resourceMappings;
    @Autowired private RepositoryRestConfiguration config;

    @Autowired
    private LinkGenerator linkGenerator;

    /* messages to validator */
    public boolean routeValidationMessageFor(MetadataDocument document) {
        if(document.getValidationState().equals(ValidationState.DRAFT)) {
            this.messageSender.queueValidationMessage(Constants.Exchanges.VALIDATION,
                                                      Constants.Queues.VALIDATION_REQUIRED,
                                                      messageFor(document));
            return true;
        } else {
            return false;
        }
    }

    /* messages to accessioner */

    public boolean routeAccessionMessageFor(MetadataDocument document) {
        // queue an accession message if the document has no uuid
        Optional<UUID> uuidOptional = Optional.of(document)
                                              .map(AbstractEntity::getUuid)
                                              .map(Uuid::getUuid);

        if(! uuidOptional.isPresent()) {
            this.messageSender.queueAccessionMessage(Constants.Exchanges.ACCESSION,
                                                     Constants.Queues.ACCESSION_REQUIRED,
                                                     messageFor(document));
            return true;
        } else {
            return false;
        }
    }

    /* messages to state tracker */

    public boolean routeStateTrackingUpdateMessageFor(MetadataDocument document) {
        // TODO: consider filtering whether the state tracker requires messages for every state change
        // let the state tracker know about everything for now
        this.messageSender.queueStateTrackingMessage(Constants.Exchanges.STATE_TRACKING,
                                                     Constants.Routing.METADATA_UPDATE,
                                                     stateTrackingMessageFor(document));
        return true;
    }

    public boolean routeStateTrackingUpdateMessageForEnvelopeEvent(SubmissionEnvelope envelope, SubmissionState state) {
        // TODO: call this when a user requests a state change on an envelope
        this.messageSender.queueStateTrackingMessage(Constants.Exchanges.STATE_TRACKING,
                                                     Constants.Routing.ENVELOPE_STATE_UPDATE,
                                                     messageFor(envelope, state));
        return true;
    }

    public boolean routeStateTrackingNewSubmissionEnvelope(SubmissionEnvelope envelope) {
        this.messageSender.queueStateTrackingMessage(Constants.Exchanges.STATE_TRACKING,
                                                     Constants.Routing.ENVELOPE_CREATE,
                                                     messageFor(envelope));
        return true;
    }

    public void sendAssayForExport(ExportData exportData) {
        messageSender.queueNewExportMessage(ASSAY_EXCHANGE, ASSAY_SUBMITTED,
                exportData.toAssaySubmittedMessage(linkGenerator));
    }

    public void sendAnalysisForExport(ExportData exportData) {
        messageSender.queueNewExportMessage(ASSAY_EXCHANGE, ANALYSIS_SUBMITTED,
                exportData.toAssaySubmittedMessage(linkGenerator));
    }

    /* messages to the upload/staging area manager */

    public boolean routeRequestUploadAreaCredentials(SubmissionEnvelope envelope) {
        this.messageSender.queueUploadManagerMessage(Constants.Exchanges.ENVELOPE_CREATED_FANOUT,
                                                     "",
                                                     messageFor(envelope));
        return true;
    }

    public boolean routeRequestUploadAreaCleanup(SubmissionEnvelope envelope) {
        this.messageSender.queueUploadManagerMessage(Constants.Exchanges.ENVELOPE_CLEANUP_FANOUT,
                "",
                messageFor(envelope));
        return true;
    }

    private MetadataDocumentMessage messageFor(MetadataDocument document) {
        return MetadataDocumentMessageBuilder.using(linkGenerator)
                                             .messageFor(document)
                                             .build();
    }

    private SubmissionEnvelopeMessage messageFor(SubmissionEnvelope envelope) {
        return SubmissionEnvelopeMessageBuilder.using(resourceMappings, config)
                                               .messageFor(envelope)
                                               .build();
    }

    private MetadataDocumentMessage stateTrackingMessageFor(MetadataDocument document) {
        Collection<String> envelopeIds = document.getSubmissionEnvelopes().stream()
                                                 .map(AbstractEntity::getId)
                                                 .collect(Collectors.toList());
        return MetadataDocumentMessageBuilder.using(linkGenerator)
                                             .messageFor(document)
                                             .withEnvelopeIds(envelopeIds)
                                             .build();
    }

    private SubmissionEnvelopeStateUpdateMessage messageFor(SubmissionEnvelope envelope, SubmissionState state) {
        SubmissionEnvelopeStateUpdateMessage message = SubmissionEnvelopeStateUpdateMessage.fromSubmissionEnvelopeMessage(messageFor(envelope));
        message.setRequestedState(state);
        return message;
    }

}
