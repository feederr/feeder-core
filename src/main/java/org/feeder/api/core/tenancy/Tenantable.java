package org.feeder.api.core.tenancy;

import static org.feeder.api.core.tenancy.TenancyJpaFilter.TENANCY_FILTER_NAME;
import static org.feeder.api.core.tenancy.TenancyJpaFilter.TENANCY_FILTER_PARAMETER;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.feeder.api.core.domain.BaseEntity;
import org.feeder.api.core.exception.TenancyRequiredException;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@FilterDef(
    name = TENANCY_FILTER_NAME,
    parameters = @ParamDef(name = TENANCY_FILTER_PARAMETER, type = "uuid-binary")
)
@Filter(name = TENANCY_FILTER_NAME, condition = "tenant_id = :" + TENANCY_FILTER_PARAMETER)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public abstract class Tenantable<ID> extends BaseEntity<ID> {

  @Column(
      name = "tenant_id",
      updatable = false,
      nullable = false
  )
  private UUID tenantId;

  @PrePersist
  public void populateTenancy() {

    UUID tenantId = TenancyRequestContextHolder.getTenancyContext().getUserId();

    if (tenantId == null) {
      throw new TenancyRequiredException("Tenancy is required for requested operation");
    }

    setTenantId(tenantId);
  }
}

