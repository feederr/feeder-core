package org.feeder.api.core.configuration;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.feeder.api.core.configuration.HierarchicalMethodSecurityConfiguration.AuthorityHierarchyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableConfigurationProperties(AuthorityHierarchyProperties.class)
public class HierarchicalMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

  private final PatternBasedRoleHierarchyImpl patternBasedRoleHierarchy =
      new PatternBasedRoleHierarchyImpl("hasAuthority");

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private AuthorityHierarchyProperties authorityHierarchyProperties;

  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setRoleHierarchy(patternBasedRoleHierarchy);
    return expressionHandler;
  }

  @Override
  public void afterSingletonsInstantiated() {

    Map<String, Object> controllers = applicationContext
        .getBeansWithAnnotation(RestController.class);

    patternBasedRoleHierarchy
        .init(controllers.values(), authorityHierarchyProperties.getAuthorityPattern());

    super.afterSingletonsInstantiated();
  }

  @Data
  @Validated
  @ConfigurationProperties("feeder.security.hierarchy")
  public static class AuthorityHierarchyProperties {

    @NotBlank
    private String authorityPattern;
  }

  @RequiredArgsConstructor
  public static class PatternBasedRoleHierarchyImpl extends RoleHierarchyImpl {

    private static final String DELIMITER = ":";

    private static final String ROLE_HIERARCHY_DELIMITER = " ";

    private static final String HIERARCHY_PREFIX_PLACEHOLDER = "*";

    private final String expression;

    public void init(Collection<Object> controllers, String hierarchyPattern) {

      if (controllers.isEmpty()) {
        return;
      }

      final Pattern pattern = Pattern.compile(expression + "\\('(.*?)'\\)");

      Set<String> prefixes = new HashSet<>();

      for (Object controller : controllers) {

        Class<?> controllerClass = getControllerClass(controller);

        log.debug("Authorization init: Controller class: {}; pattern: {}; hierarchyPattern: {}",
            controllerClass.getSimpleName(), pattern.pattern(), hierarchyPattern);

        for (Method handlerMethod : controllerClass.getDeclaredMethods()) {

          if (handlerMethod.isAnnotationPresent(PreAuthorize.class)) {

            final Matcher matcher = pattern
                .matcher(handlerMethod.getAnnotation(PreAuthorize.class).value());

            while (matcher.find()) {

              final String matched = matcher.group(1); // extract an authority from hasAuthority(..)
              final String[] split = matched.split(DELIMITER);

              if (split.length != 2) {
                throw new IllegalStateException(
                    "Unknown @PreAuthorize expression value: " + matched);
              }

              log.debug("Authorization init: Controller class: {}; prefix: {}",
                  controllerClass.getSimpleName(), split[0]);

              prefixes.add(split[0]);
            }
          }
        }
      }

      log.debug("Authorization init: prefixes: {}", Arrays.toString(prefixes.toArray()));

      if (!prefixes.isEmpty()) {
        String builtHierarchy = buildHierarchy(prefixes, hierarchyPattern);
        log.debug("Authorization init: builtHierarchy: {}", builtHierarchy);
        setHierarchy(builtHierarchy);
      }
    }

    private static Class<?> getControllerClass(Object controller) {

      Class<?> controllerClass = controller.getClass();

      if (org.springframework.aop.support.AopUtils.isAopProxy(controller)) {
        controllerClass = org.springframework.aop.support.AopUtils.getTargetClass(controller);
      }

      return controllerClass;
    }

    private static String buildHierarchy(Set<String> prefixes, String hierarchyPattern) {
      return prefixes.stream()
          .map(p -> hierarchyPattern.replace(HIERARCHY_PREFIX_PLACEHOLDER, p))
          .collect(Collectors.joining(ROLE_HIERARCHY_DELIMITER));
    }
  }
}
