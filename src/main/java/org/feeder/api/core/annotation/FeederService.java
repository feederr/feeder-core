package org.feeder.api.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.feeder.api.core.configuration.AsyncApplicationEventConfiguration;
import org.feeder.api.core.configuration.ExceptionHandlingConfiguration;
import org.feeder.api.core.configuration.JpaAuditingConfiguration;
import org.feeder.api.core.configuration.ResourceServerConfiguration;
import org.feeder.api.core.configuration.TenancyConfiguration;
import org.feeder.api.core.repository.EntityClassAwareRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({
    ExceptionHandlingConfiguration.class,
    AsyncApplicationEventConfiguration.class,
    JpaAuditingConfiguration.class,
    ResourceServerConfiguration.class,
    TenancyConfiguration.class
})
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = EntityClassAwareRepository.class)
public @interface FeederService {

}
