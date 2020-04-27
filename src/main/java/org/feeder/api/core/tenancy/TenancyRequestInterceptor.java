package org.feeder.api.core.tenancy;

import static org.feeder.api.core.tenancy.AccessTokenHelper.getUserId;
import static org.feeder.api.core.tenancy.TenancyRequestContextHolder.clearTenancyContext;
import static org.feeder.api.core.tenancy.TenancyRequestContextHolder.setTenancyContext;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.feeder.api.core.exception.TenancyRequiredException;
import org.feeder.api.core.tenancy.TenancyRequestContextHolder.TenancyContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

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

    UUID userId = getUserId().orElseThrow(() -> new TenancyRequiredException(
        String.format("Tenancy is required for requested URL: [%s] and method: [%s]",
            request.getRequestURL(), request.getMethod()))
    );

    setTenancyContext(new TenancyContext(userId));

    return true;
  }

  private boolean tenancyNotRequired(final HttpServletRequest request) {
    return tenancyFreePaths.stream()
        .anyMatch(path -> antPathMatcher.match(path, request.getRequestURI()));
  }
}
