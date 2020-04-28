package org.feeder.api.core.repository;

import javax.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public class EntityClassAwareRepository<ENTITY, ID> extends SimpleJpaRepository<ENTITY, ID> {

  private final JpaEntityInformation<ENTITY, ID> entityInformation;

  public EntityClassAwareRepository(
      JpaEntityInformation<ENTITY, ID> entityInformation,
      EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityInformation = entityInformation;
  }

  public Class<ENTITY> getEntityClass() {
    return entityInformation.getJavaType();
  }
}
