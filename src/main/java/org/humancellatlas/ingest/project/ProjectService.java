package org.humancellatlas.ingest.project;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.humancellatlas.ingest.bundle.BundleManifest;
import org.humancellatlas.ingest.bundle.BundleManifestRepository;
import org.humancellatlas.ingest.bundle.BundleType;
import org.humancellatlas.ingest.core.Uuid;
import org.humancellatlas.ingest.core.service.MetadataCrudService;
import org.humancellatlas.ingest.core.service.MetadataUpdateService;
import org.humancellatlas.ingest.project.exception.NonEmptyProject;
import org.humancellatlas.ingest.query.MetadataCriteria;
import org.humancellatlas.ingest.submission.SubmissionEnvelope;
import org.humancellatlas.ingest.submission.SubmissionEnvelopeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;


/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 05/09/17
 */
@Service
@RequiredArgsConstructor
@Getter
public class ProjectService {

    //Helper class for capturing copies of a Project and all Submission Envelopes related to them.
    private static class ProjectBag {

        private final Set<Project> projects;
        private final Set<SubmissionEnvelope> submissionEnvelopes;

        public ProjectBag(Set<Project> projects, Set<SubmissionEnvelope> submissionEnvelopes) {
            this.projects = projects;
            this.submissionEnvelopes = submissionEnvelopes;
        }

    }

    private final @NonNull SubmissionEnvelopeRepository submissionEnvelopeRepository;
    private final @NonNull ProjectRepository projectRepository;
    private final @NonNull MetadataCrudService metadataCrudService;
    private final @NonNull MetadataUpdateService metadataUpdateService;
    private final @NonNull BundleManifestRepository bundleManifestRepository;

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected Logger getLog() {
        return log;
    }

    public Project addProjectToSubmissionEnvelope(SubmissionEnvelope submissionEnvelope, Project project) {
        if(! project.getIsUpdate()) {
            return metadataCrudService.addToSubmissionEnvelopeAndSave(project, submissionEnvelope);
        } else {
            return metadataUpdateService.acceptUpdate(project, submissionEnvelope);
        }
    }

    public Project linkProjectSubmissionEnvelope(SubmissionEnvelope submissionEnvelope, Project project) {
        final String projectId = project.getId();
        project.addToSubmissionEnvelopes(submissionEnvelope);
        projectRepository.save(project);

        projectRepository.findByUuidUuidAndIsUpdateFalse(project.getUuid().getUuid()).ifPresent(projectByUuid -> {
            if (!projectByUuid.getId().equals(projectId)) {
                projectByUuid.addToSubmissionEnvelopes(submissionEnvelope);
                projectRepository.save(projectByUuid);
            }
        });
        return project;
    }

    public Page<BundleManifest> findBundleManifestsByProjectUuidAndBundleType(Uuid projectUuid, BundleType bundleType,
            Pageable pageable) {
        return this.projectRepository
                .findByUuidUuidAndIsUpdateFalse(projectUuid.getUuid())
                .map(project -> bundleManifestRepository.findBundleManifestsByProjectAndBundleType(project,
                        bundleType, pageable))
                .orElseThrow(() -> {
                    throw new ResourceNotFoundException(String.format("Project with UUID %s not found",
                            projectUuid.getUuid().toString()));
                });
    }

    public Page<Project> findByCriteria(List<MetadataCriteria> criteriaList, Boolean andCriteria, Pageable pageable){
        return this.projectRepository.findByCriteria(criteriaList, andCriteria, pageable);
    }

    public Page<SubmissionEnvelope> getSubmissionEnvelopes(Project project, Pageable pageable) {
        Set<SubmissionEnvelope> envelopes = gather(project).submissionEnvelopes;
        return new PageImpl<>(new ArrayList<>(envelopes), pageable, envelopes.size());
    }

    public Set<SubmissionEnvelope> getSubmissionEnvelopes(Project project) {
        return gather(project).submissionEnvelopes;
    }

    public void delete(Project project) throws NonEmptyProject {
        ProjectBag projectBag = gather(project);
        if (projectBag.submissionEnvelopes.isEmpty()) {
            projectBag.projects.forEach(projectRepository::delete);
        } else {
            throw new NonEmptyProject();
        }
    }

    private ProjectBag gather(Project project) {
        Set<SubmissionEnvelope> envelopes = new HashSet<>();
        Set<Project> projects = this.projectRepository.findByUuid(project.getUuid()).collect(toSet());
        projects.forEach(copy -> {
            envelopes.addAll(copy.getSubmissionEnvelopes());
            envelopes.add(copy.getSubmissionEnvelope());
        });

        //ToDo: Find a better way of ensuring that DBRefs to deleted objects aren't returned.
        envelopes.removeIf(env -> env == null || env.getSubmissionState() == null);
        return new ProjectBag(projects, envelopes);
    }

}
