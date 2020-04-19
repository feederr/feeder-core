package org.feeder.api.core.configuration;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  @Value("${spring.application.name}")
  private String resource;

  @Override
  @SneakyThrows
  public void configure(ResourceServerSecurityConfigurer configurer) {
    configurer.resourceId(resource);
  }

  @Override
  @SneakyThrows
  public void configure(HttpSecurity http) {
    http.authorizeRequests()
        .anyRequest().authenticated();
  }
}
