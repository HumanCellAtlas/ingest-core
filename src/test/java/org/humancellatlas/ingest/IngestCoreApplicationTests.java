package org.humancellatlas.ingest;

import org.humancellatlas.ingest.core.MigrationConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IngestCoreApplicationTests {
	@MockBean
	MigrationConfiguration migrationConfiguration;

	@Test
	public void contextLoads() {
	}

}
