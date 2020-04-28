package org.feeder.api.core.tenancy;

import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityManager;
import org.feeder.api.core.exception.TenancyMismatchException;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public class TenancyAwareRepository<ENTITY, ID> extends SimpleJpaRepository<ENTITY, ID> {

  private final JpaEntityInformation<ENTITY, ID> entityInformation;

  public TenancyAwareRepository(
      JpaEntityInformation<ENTITY, ID> entityInformation,
      EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityInformation = entityInformation;
  }

  // SimpleJpaRepository.findById() does not take tenancy into account by default
  @Override
  @Transactional(readOnly = true)
  public Optional<ENTITY> findById(ID id) {
    return super.findById(id)
        .filter(result -> {

          final UUID tenantId = TenancyRequestContextHolder.getTenancyContext().getUserId();

          if (tenantId != null && result instanceof Tenantable) {
            return tenantId.equals(((Tenantable) result).getTenantId());
          }

          return false;
        });
  }

  // SimpleJpaRepository.save() does not take tenancy into account by default
  @Override
  @Transactional
  public <S extends ENTITY> S save(S entity) {
    validate(entity);
    return super.save(entity);
  }

  // SimpleJpaRepository.delete() does not take tenancy into account by default
  @Override
  @Transactional
  public void delete(ENTITY entity) {
    validate(entity);
    super.delete(entity);
  }

  public Class<ENTITY> getEntityClass() {
    return entityInformation.getJavaType();
  }

  private <CHILD extends ENTITY> void validate(CHILD entity) {

    if (TenancyRequestContextHolder.getTenancyContext().getUserId() != null
        && entity instanceof Tenantable) {

      ID id = entityInformation.getId(entity);

      if (id != null && !isNew(entity)) {

        Optional<ENTITY> existingEntityOpt = findById(id);

        if (existingEntityOpt.isPresent()) {

          Tenantable existingEntity = (Tenantable) existingEntityOpt.get();

          if (tenancyMismatch(existingEntity)) {
            throw new TenancyMismatchException(
                String.format(
                    "Tenancy mismatch. Actual tenant (user_id): %s. Required tenant (user_id): %s",
                    existingEntity.getTenantId(),
                    TenancyRequestContextHolder.getTenancyContext().getUserId()
                )
            );
          }
        }
      }
    }
  }

  private boolean tenancyMismatch(Tenantable entity) {
    return !entity.getTenantId()
        .equals(TenancyRequestContextHolder.getTenancyContext().getUserId());
  }

  private <CHILD extends ENTITY> boolean isNew(CHILD entity) {
    return entity instanceof Persistable && ((Persistable<?>) entity).isNew();
  }
}
