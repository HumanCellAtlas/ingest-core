package org.humancellatlas.ingest.project.web;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.humancellatlas.ingest.bundle.BundleManifest;
import org.humancellatlas.ingest.core.Uuid;
import org.humancellatlas.ingest.project.Project;
import org.humancellatlas.ingest.project.ProjectService;
import org.humancellatlas.ingest.submission.SubmissionEnvelope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.PersistentEntityResource;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Javadocs go here!
 *
 * @author Tony Burdett
 * @date 05/09/17
 */
@RepositoryRestController
@ExposesResourceFor(Project.class)
@RequiredArgsConstructor
@Getter
public class ProjectController {
    private final @NonNull ProjectService projectService;
    private final @NonNull PagedResourcesAssembler pagedResourcesAssembler;

    @RequestMapping(path = "submissionEnvelopes/{sub_id}/projects", method = RequestMethod.POST)
    ResponseEntity<Resource<?>> addProjectToEnvelope(@PathVariable("sub_id") SubmissionEnvelope submissionEnvelope,
                                                     @RequestBody Project project,
                                                     @RequestParam("updatingUuid") Optional<UUID> updatingUuid,
                                                     PersistentEntityResourceAssembler assembler) {
        updatingUuid.ifPresent(uuid -> project.setUuid(new Uuid(uuid.toString())));
        Project entity = getProjectService().addProjectToSubmissionEnvelope(submissionEnvelope, project);
        PersistentEntityResource resource = assembler.toFullResource(entity);
        return ResponseEntity.accepted().body(resource);
    }

    @RequestMapping(path = "submissionEnvelopes/{sub_id}/projects/{id}", method = RequestMethod.PUT)
    ResponseEntity<Resource<?>> linkProjectToEnvelope(@PathVariable("sub_id") SubmissionEnvelope submissionEnvelope,
                                                      @PathVariable("id") Project project,
                                                     PersistentEntityResourceAssembler assembler) {
        Project entity = getProjectService().addProjectToSubmissionEnvelope(submissionEnvelope, project);
        PersistentEntityResource resource = assembler.toFullResource(entity);
        return ResponseEntity.accepted().body(resource);
    }


    @RequestMapping(path = "/projects/{id}/analysisBundles", method = RequestMethod.GET)
    ResponseEntity<?> findAnalysisBundles( @PathVariable("id") Project project,
                                          Pageable pageable,
                                          final PersistentEntityResourceAssembler resourceAssembler) {
        Page<BundleManifest> bundleManifests = projectService.findBundlesByProject(project, Boolean.FALSE, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(bundleManifests, resourceAssembler));
    }

    @RequestMapping(path = "/projects/{id}/primaryBundles", method = RequestMethod.GET)
    ResponseEntity<?> findPrimaryBundles( @PathVariable("id") Project project,
                                          Pageable pageable,
                                          final PersistentEntityResourceAssembler resourceAssembler) {
        Page<BundleManifest> bundleManifests = projectService.findBundlesByProject(project, Boolean.TRUE, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(bundleManifests, resourceAssembler));
    }

    @RequestMapping(path = "/projects/{id}/bundles", method = RequestMethod.GET)
    ResponseEntity<?> findAllBundles( @PathVariable("id") Project project,
                                          Pageable pageable,
                                          final PersistentEntityResourceAssembler resourceAssembler) {
        Page<BundleManifest> bundleManifests = projectService.findAllBundlesByProject(project, pageable);
        return ResponseEntity.ok(pagedResourcesAssembler.toResource(bundleManifests, resourceAssembler));
    }


}