package org.feeder.api.core.configuration;

import org.feeder.api.core.repository.EntityClassAwareRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.feeder.api", repositoryBaseClass = EntityClassAwareRepository.class)
public class JpaRepositoryConfiguration {

}
