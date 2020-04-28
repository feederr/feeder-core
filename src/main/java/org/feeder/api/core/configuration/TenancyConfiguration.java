package org.feeder.api.core.configuration;

import javax.persistence.EntityManager;
import org.feeder.api.core.tenancy.TenancyAwareRepository;
import org.feeder.api.core.tenancy.TenancyAspect;
import org.feeder.api.core.tenancy.TenancyJpaFilter;
import org.feeder.api.core.tenancy.TenancyRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = "org.feeder.api", repositoryBaseClass = TenancyAwareRepository.class)
public class TenancyConfiguration implements WebMvcConfigurer {

  @Autowired
  private EntityManager entityManager;

  @Bean
  public TenancyRequestInterceptor tenancyRequestInterceptor() {
    return new TenancyRequestInterceptor();
  }

  @Bean
  public TenancyAspect tenancyAspect() {
    return new TenancyAspect(tenancyJpaFilter());
  }

  @Bean
  public TenancyJpaFilter tenancyJpaFilter() {
    return new TenancyJpaFilter(entityManager);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tenancyRequestInterceptor());
  }
}
