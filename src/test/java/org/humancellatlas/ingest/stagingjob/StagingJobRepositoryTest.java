package org.humancellatlas.ingest.stagingjob;

import org.humancellatlas.ingest.config.MigrationConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StagingJobRepositoryTest {
    @MockBean
    MigrationConfiguration migrationConfiguration;

    @Autowired
    StagingJobRepository stagingJobRepository;

    @Test
    public void testJpaExceptionWhenInsertingMultipleCompoundKey() {
        UUID testStagingAreaUuid = UUID.randomUUID();
        String testFileName = "test.fastq.gz";

        stagingJobRepository.save(new StagingJob(testStagingAreaUuid, testFileName));

        assertThatExceptionOfType(DuplicateKeyException.class).isThrownBy(() -> {
            stagingJobRepository.save(new StagingJob(testStagingAreaUuid, testFileName));
        });
    }

    @Test
    public void testRegisteringJobsWithDifferentCompoundKey() {
        UUID testStagingAreaUuid_1 = UUID.randomUUID();
        String testFileName_1 = "test_1.fastq.gz";

        UUID testStagingAreaUuid_2 = UUID.randomUUID();
        String testFileName_2 = "test_2.fastq.gz";

        stagingJobRepository.save(new StagingJob(testStagingAreaUuid_1, testFileName_1));
        stagingJobRepository.save(new StagingJob(testStagingAreaUuid_1, testFileName_2));

        stagingJobRepository.save(new StagingJob(testStagingAreaUuid_2, testFileName_1));
        stagingJobRepository.save(new StagingJob(testStagingAreaUuid_2, testFileName_2));
    }
}
