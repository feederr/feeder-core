package org.feeder.api.core.tenancy;

import static org.feeder.api.core.tenancy.TenancyRequestContextHolder.clearTenancyContext;
import static org.feeder.api.core.tenancy.TenancyRequestContextHolder.setTenancyContext;
import static org.feeder.api.core.util.TokenHelper.extractUserId;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.feeder.api.core.tenancy.TenancyRequestContextHolder.TenancyContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Slf4j
public class TenancyRequestInterceptor extends HandlerInterceptorAdapter {

  private AntPathMatcher antPathMatcher = new AntPathMatcher();

  private Set<String> tenancyFreePaths = Set.of(
      "/**.ico", "/**.css", "/**.js", "/**.html",
      "/actuator/**",
      "/error"
  );

  @Override
  @SneakyThrows
  public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
      final Object handler) {

    clearTenancyContext();

    if (tenancyNotRequired(request)) {
      return true;
    }

    // lets assume for now that all endpoints (except static) do not require tenancy
    // tenancy validation will happen on JPA level
    Optional<UUID> userIdOpt = extractUserId();

    if (userIdOpt.isPresent()) {

      UUID userId = userIdOpt.get();
      log.debug("Populating tenancy context (user_id) with user id: {}", userId);
      setTenancyContext(new TenancyContext(userId));

    } else {

      log.debug("Tenancy (user_id) not found in token. Tenancy context not populated");

    }

    return true;
  }

  private boolean tenancyNotRequired(final HttpServletRequest request) {
    return tenancyFreePaths.stream()
        .anyMatch(path -> antPathMatcher.match(path, request.getRequestURI()));
  }
}
