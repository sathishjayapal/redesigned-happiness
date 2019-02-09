package org.contractor.webmod;

import org.contractor.webmod.config.ContractorWebAppErrorRegistrar;
import org.contractor.webmod.config.ContractorWebAppWebConfigurer;
import org.contractor.webmod.config.FavIconConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JdbcTemplateAutoConfiguration.class})
public class MainApp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        final SpringApplication springApplication =
                new SpringApplication(ContractorWebAppErrorRegistrar.class,
                        ContractorWebAppWebConfigurer.class,FavIconConfiguration.class,
                        MainApp.class);
        springApplication.run(args);
    }
}