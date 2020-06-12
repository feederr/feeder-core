package org.feeder.api.core.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

@Slf4j
@Configuration
public class DispatcherConfiguration {

  @Bean
  public DispatcherServlet dispatcherServlet() {

    log.debug("Configuring dispatcher servlet..");

    final DispatcherServlet dispatcherServlet = new DispatcherServlet();
    dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

    return dispatcherServlet;
  }
}
