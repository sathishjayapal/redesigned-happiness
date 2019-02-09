package org.contractor.service;

import org.contractor.service.config.ContractTimerWebServiceConfig;
import org.contractor.service.config.ContractorTimerWebContextConfig;
import org.contractor.service.setup.ContractTimerWebApplicationContextInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {
        ErrorMvcAutoConfiguration.class
})
public class ContractorTimerWebRunner extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ContractorTimerWebRunner.class);
    }

    public static void main(final String... args) {
        final SpringApplication springApplication = new SpringApplication(
                ContractorTimerWebContextConfig.class,
                ContractTimerWebServiceConfig.class
        );
        springApplication.addInitializers(new ContractTimerWebApplicationContextInitializer());
        springApplication.run(args);
    }
}
