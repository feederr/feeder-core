package org.feeder.api.core.configuration;

import java.util.Map;
import javax.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableWebSecurity
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  @Value("${spring.application.name}")
  private String resource;

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  @SneakyThrows
  public void configure(ResourceServerSecurityConfigurer configurer) {
    configurer.resourceId(resource);
  }

  // @formatter:off
  @Override
  @SneakyThrows
  public void configure(HttpSecurity http) {
    http.csrf().disable()
          .authorizeRequests()
          .anyRequest().authenticated()
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.NEVER);
  }
  // @formatter:on

  @PostConstruct
  public void replaceDefaultConverter() {
    JwtAccessTokenConverter jwtAccessTokenConverterBean = applicationContext
        .getBean(JwtAccessTokenConverter.class);
    jwtAccessTokenConverterBean.setAccessTokenConverter(new CustomTokenConverter());
  }

  private static class CustomTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
      OAuth2Authentication auth2Authentication = super.extractAuthentication(map);
      auth2Authentication.setDetails(map);
      return auth2Authentication;
    }
  }
}
