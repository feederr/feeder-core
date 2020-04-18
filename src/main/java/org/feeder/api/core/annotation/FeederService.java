package org.feeder.api.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.feeder.api.core.configuration.AsyncApplicationEventConfiguration;
import org.feeder.api.core.configuration.ExceptionHandlingConfiguration;
import org.feeder.api.core.configuration.JpaAuditingConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({
    ExceptionHandlingConfiguration.class,
    AsyncApplicationEventConfiguration.class,
    JpaAuditingConfiguration.class
})
@SpringBootApplication
public @interface FeederService {

}
