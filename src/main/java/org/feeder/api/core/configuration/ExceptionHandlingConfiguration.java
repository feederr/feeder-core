package org.feeder.api.core.configuration;

import org.feeder.api.core.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = GlobalExceptionHandler.class)
public class ExceptionHandlingConfiguration {

}
