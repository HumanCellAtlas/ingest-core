package org.humancellatlas.ingest.server;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.humancellatlas.ingest.config.ConfigurationService;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
@AllArgsConstructor
public class JettyConfig {

    private final @NonNull ConfigurationService configurationService;

    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory() {
        int numThreads = configurationService.getServerWorkerThreads();
        JettyEmbeddedServletContainerFactory jettyContainer = new JettyEmbeddedServletContainerFactory();
        ThreadPool threadPool = new ExecutorThreadPool(Executors.newFixedThreadPool(numThreads));
        jettyContainer.setThreadPool(threadPool);
        return jettyContainer;
    }
}
