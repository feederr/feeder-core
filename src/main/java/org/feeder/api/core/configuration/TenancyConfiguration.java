package org.feeder.api.core.configuration;

import org.feeder.api.core.tenancy.TenancyRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TenancyConfiguration implements WebMvcConfigurer {

  @Bean
  public TenancyRequestInterceptor tenancyRequestInterceptor() {
    return new TenancyRequestInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tenancyRequestInterceptor());
  }
}
