package org.feeder.api.core.tenancy;

import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Filter;
import org.hibernate.Session;

@Slf4j
@RequiredArgsConstructor
public class TenancyJpaFilter {

  public static final String TENANCY_FILTER_NAME = "tenancy_filter";

  public static final String TENANCY_FILTER_PARAMETER = "tenant_id";

  private final EntityManager entityManager;

  public boolean supports(Class<?> clazz) {
    return Tenantable.class.isAssignableFrom(clazz);
  }

  public void disable() {
    if (entityManager.isJoinedToTransaction()) {
      final Session session = entityManager.unwrap(Session.class);

      if (session.getEnabledFilter(TENANCY_FILTER_NAME) != null) {
        log.debug("Disabling Tenancy Jpa Filter: {}", TENANCY_FILTER_NAME);
        session.disableFilter(TENANCY_FILTER_NAME);
      }
    }
  }

  public void enable(UUID tenantId) {

    final Session session = entityManager.unwrap(Session.class);
    final Filter filter = session.getEnabledFilter(TENANCY_FILTER_NAME);

    if (filter != null) {

      log.debug("Tenancy Jpa Filter for {} already enabled. Setting filter param: {}",
          TENANCY_FILTER_PARAMETER, tenantId);

      filter.setParameter(TENANCY_FILTER_PARAMETER, tenantId);
    } else {

      log.debug("Enabling Tenancy Jpa Filter for {}. Setting filter param: {}",
          TENANCY_FILTER_PARAMETER, tenantId);

      session.enableFilter(TENANCY_FILTER_NAME)
          .setParameter(TENANCY_FILTER_PARAMETER, tenantId);
    }
  }
}
