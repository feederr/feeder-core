package org.feeder.api.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.feeder.api.core.configuration.AsyncApplicationEventConfiguration;
import org.feeder.api.core.configuration.DispatcherConfiguration;
import org.feeder.api.core.configuration.ExceptionHandlingConfiguration;
import org.feeder.api.core.configuration.HierarchicalMethodSecurityConfiguration;
import org.feeder.api.core.configuration.JpaAuditingConfiguration;
import org.feeder.api.core.configuration.KafkaConfiguration;
import org.feeder.api.core.configuration.ResourceServerConfiguration;
import org.feeder.api.core.configuration.TenancyConfiguration;
import org.feeder.api.core.configuration.WebConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({
    ExceptionHandlingConfiguration.class,
    AsyncApplicationEventConfiguration.class,
    JpaAuditingConfiguration.class,
    ResourceServerConfiguration.class,
    HierarchicalMethodSecurityConfiguration.class,
    TenancyConfiguration.class,
    KafkaConfiguration.class,
    DispatcherConfiguration.class,
    WebConfiguration.class
})
@SpringBootApplication
public @interface FeederService {

}
