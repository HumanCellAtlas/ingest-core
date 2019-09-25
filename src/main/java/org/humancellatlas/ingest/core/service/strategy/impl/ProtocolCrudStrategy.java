package org.humancellatlas.ingest.core.service.strategy.impl;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.humancellatlas.ingest.core.service.strategy.MetadataCrudStrategy;
import org.humancellatlas.ingest.protocol.Protocol;
import org.humancellatlas.ingest.protocol.ProtocolRepository;
import org.humancellatlas.ingest.submission.SubmissionEnvelope;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class ProtocolCrudStrategy implements MetadataCrudStrategy<Protocol> {
    private final @NonNull ProtocolRepository protocolRepository;

    @Override
    public Protocol saveMetadataDocument(Protocol document) {
        return protocolRepository.save(document);
    }

    @Override
    public Protocol findMetadataDocument(String id) {
        return protocolRepository.findById(id)
                                 .orElseThrow(() -> {
                                     throw new ResourceNotFoundException();
                                 });
    }

    @Override
    public Protocol findOriginalByUuid(String uuid) {
        return protocolRepository.findByUuidUuidAndIsUpdateFalse(UUID.fromString(uuid))
                                 .orElseThrow(() -> {
                                     throw new ResourceNotFoundException();
                                 });
    }

    @Override
    public Stream<Protocol> findBySubmissionEnvelope(SubmissionEnvelope submissionEnvelope) {
        return protocolRepository.findBySubmissionEnvelope(submissionEnvelope);
    }
}