package org.humancellatlas.ingest.errors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.zalando.problem.Problem;

import java.net.URI;

@Data
@NoArgsConstructor
@Setter(AccessLevel.PACKAGE)
@JsonIgnoreProperties({"parameters","status"})
public class IngestError implements Problem {
    private URI type;
    private String title;
    private String detail;
    private URI instance;

    IngestError(Problem problem) {
        this.type = problem.getType();
        this.title = problem.getTitle();
        this.detail = problem.getDetail();
        this.instance = problem.getInstance();
    }
}