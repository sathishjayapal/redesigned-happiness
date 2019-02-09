package org.contractor.core.config;

import org.contractor.core.service.impl.CalculateDateServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Contract_Timer_CoreConfig {
    @Bean
    public CalculateDateServiceImpl calculateDateService(){
        return new CalculateDateServiceImpl();
    }
}
