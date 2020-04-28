package org.feeder.api.core.tenancy;

import static org.feeder.api.core.tenancy.TenancyJpaFilter.TENANCY_FILTER_NAME;
import static org.feeder.api.core.tenancy.TenancyJpaFilter.TENANCY_FILTER_PARAMETER;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.feeder.api.core.domain.BaseEntity;
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

  @NotNull
  @Column(
      name = "tenant_id",
      updatable = false,
      nullable = false,
      unique = true
  )
  private UUID tenantId;

  @PrePersist
  public void populateTenancy() {
    setTenantId(TenancyRequestContextHolder.getTenancyContext().getUserId());
  }
}

