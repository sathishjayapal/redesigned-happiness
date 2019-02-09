package org.contractor.service.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ImportResource("classpath*:ContractTimerWebContextConfig.xml")
public class ContractorTimerWebContextConfig {
  public ContractorTimerWebContextConfig() {
    super();
  }
  @Bean
  public static PropertySourcesPlaceholderConfigurer properties() {
    final PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
    return pspc;
  }
}
