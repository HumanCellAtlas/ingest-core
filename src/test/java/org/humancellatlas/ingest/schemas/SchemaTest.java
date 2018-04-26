package org.humancellatlas.ingest.schemas;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.humancellatlas.ingest.schemas.schemascraper.SchemaScraper;
import org.humancellatlas.ingest.schemas.schemascraper.impl.S3BucketSchemaScraper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.*;


/**
 * Created by rolando on 19/04/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class SchemaTest {
    @Autowired SchemaService schemaService;
    @Autowired SchemaRepository schemaRepository;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8088);

    @Test
    public void testSchemaScrape() throws Exception {
        // given
        // an s3 bucket files listing as XML
        SchemaScraper schemaScraper = new S3BucketSchemaScraper();

        // when
        stubFor(
                get(urlEqualTo("/"))
                        .willReturn(aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/xml")
                                            .withBody(new String(Files.readAllBytes(Paths.get(new File(".").getAbsolutePath() + "/src/test/resources/testfiles/TestBucketListing.xml"))))));

        Collection<URI> mockSchemaUris = schemaScraper.getAllSchemaURIs(URI.create("http://localhost:8088"));

        // we know there are 108 schemas in the test file
        assert mockSchemaUris.size() == 107;
    }

    @Test
    public void testSchemaParse_BundleUris() throws Exception {
        try {
            // when
            schemaService.schemaDescriptionFromSchemaUris(Arrays.asList(URI.create("bundle/1.2.3/biomaterial"),
                                                                        URI.create("bundle/2.3.4/links"),
                                                                        URI.create("bundle/1.0/protocols")));
        } catch (Exception e) {
            assert false;
        }

        assert true;
    }

    @Test
    public void testSchemaParse_ModuleUris() throws Exception {
        try {
            // when
            schemaService.schemaDescriptionFromSchemaUris(Arrays.asList(URI.create("module/biomaterial/5.1.0/growth_condition"),
                                                                        URI.create("module/ontology/5.0.0/biological_macromolecule_ontology"),
                                                                        URI.create("module/process/5.1.0/purchased_reagents")));
        } catch (Exception e) {
            assert false;
        }

        assert true;
    }

    @Test
    public void testSchemaParse_TypeUris() throws Exception {
        try {
            // when
            schemaService.schemaDescriptionFromSchemaUris(Arrays.asList(URI.create("type/biomaterial/5.0.1/cell_line"),
                                                                        URI.create("type/biomaterial/5.1.0/organoid"),
                                                                        URI.create("type/file/5.0.0/sequence_file")));
        } catch (Exception e) {
            assert false;
        }

        assert true;
    }

    @Test
    public void testSchemaParse_SubdomainTypeUris() throws Exception {
        try {
            // when
            schemaService.schemaDescriptionFromSchemaUris(Arrays.asList(URI.create("type/process/biomaterial_collection/5.1.0/collection_process"),
                                                                        URI.create("type/process/sequencing/5.0.0/sequencing_process"),
                                                                        URI.create("type/process/sequencing/5.1.0/sequencing_process")));
        } catch (Exception e) {
            assert false;
        }

        assert true;
    }

    @Test
    public void testSchemaParse() throws Exception {
        // pre-given
        SchemaScraper schemaScraper = new S3BucketSchemaScraper();

        stubFor(
                get(urlEqualTo("/"))
                        .willReturn(aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/xml")
                                            .withBody(new String(Files.readAllBytes(Paths.get(new File(".").getAbsolutePath() + "/src/test/resources/testfiles/TestBucketListing.xml"))))));
        // given
        Collection<URI> mockSchemaUris = schemaScraper.getAllSchemaURIs(URI.create("http://localhost:8088"));

        try {
            // when
            schemaService.schemaDescriptionFromSchemaUris(mockSchemaUris);
        } catch (Exception e) {
            assert false;
        }

        assert true;
    }

    @Test
    public void testUpdateSchemasCollection() throws Exception {
        // pre-given

        stubFor(
                get(urlEqualTo("/"))
                        .willReturn(aResponse()
                                            .withStatus(200)
                                            .withHeader("Content-Type", "application/xml")
                                            .withBody(new String(Files.readAllBytes(Paths.get(new File(".").getAbsolutePath() + "/src/test/resources/testfiles/TestBucketListing.xml"))))));

        schemaService.updateSchemasCollection();
        // we know that there are 41 distinct schemas in the test file
        verify(schemaRepository, times(41)).save(any(Schema.class));

    }


    @Configuration
    static class ContextConfiguration {
        @Autowired SchemaScraper schemaScraper;
        @Autowired Environment environment;
        @Autowired SchemaRepository schemaRepository;

        @Bean
        SchemaService schemaService() {
            return new SchemaService(schemaRepository, schemaScraper, environment);
        }

        @Bean
        SchemaScraper schemaScraper() {
            return new S3BucketSchemaScraper();
        }

        @Bean Environment environment() {
            MockEnvironment mockEnvironment = new MockEnvironment();
            mockEnvironment.setProperty("SCHEMA_BASE_URI", "http://localhost:8088");
            return mockEnvironment;
        }

        @Bean
        SchemaRepository schemaRepository() {
            return Mockito.mock(SchemaRepository.class);
        }
    }

}
