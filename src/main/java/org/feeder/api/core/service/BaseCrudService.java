package org.feeder.api.core.service;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.feeder.api.core.domain.BaseEntity;
import org.feeder.api.core.exception.EntityNotFoundException;
import org.feeder.api.core.mapper.BaseMapper;
import org.feeder.api.core.search.CustomRsqlVisitor;
import org.feeder.api.core.search.JpaSpecificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class BaseCrudService<ENTITY extends BaseEntity<UUID>, REQUEST_VO, RESPONSE_VO> {

  protected abstract BaseMapper<ENTITY, REQUEST_VO, RESPONSE_VO> getMapper();

  protected abstract JpaSpecificationRepository<ENTITY, UUID> getRepository();

  @Transactional(propagation = Propagation.REQUIRED)
  public RESPONSE_VO create(REQUEST_VO vo, UUID id, Object... args) {

    log.debug("Create {} = {}", getEntityClass().getSimpleName(), id);

    ENTITY createdEntity = createEntity(vo, id, args);

    return getMapper().toResponseVO(createdEntity, args);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public RESPONSE_VO get(UUID id, Object... args) {

    log.debug("Get {} = {}", getEntityClass().getSimpleName(), id);

    ENTITY fetchedEntity = getEntity(id, args);

    return getMapper().toResponseVO(fetchedEntity, args);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Page<RESPONSE_VO> getAllBySpec(String predicate, Pageable pageable) {

    log.debug("Get {} on page: {} with size: {} and predicate: {}",
        getEntityClass().getSimpleName(), pageable.getPageNumber(), pageable.getPageSize(),
        predicate);

    Page<ENTITY> entities;

    if (predicate == null) {
      entities = getEntities(pageable);
    } else {

      Node root = new RSQLParser().parse(predicate);
      Specification<ENTITY> specification = root.accept(new CustomRsqlVisitor<>());

      if (specification == null) {
        log.debug("Unable to build specification");
        entities = getEntities(pageable);
      } else {
        entities = getEntitiesBySpec(specification, pageable);
      }

    }

    return entities.map(
        entity -> getMapper().toResponseVO(entity)
    );
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public RESPONSE_VO update(REQUEST_VO vo, UUID id, Object... args) {

    log.debug("Update {} = {}", getEntityClass().getSimpleName(), id);

    ENTITY fetchedEntity = getEntity(id);
    ENTITY updatedEntity = updateEntity(fetchedEntity, vo, args);

    return getMapper().toResponseVO(updatedEntity, args);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void delete(UUID id, Object... args) {

    log.debug("Delete {} = {}", getEntityClass().getSimpleName(), id);

    ENTITY entity = getEntity(id, args);

    getRepository().delete(entity);
  }

  protected ENTITY createEntity(REQUEST_VO vo, UUID id, Object... args) {

    ENTITY entity = getMapper().toEntity(vo, id);

    entity.setNew(true);

    return getRepository().save(entity);
  }

  protected ENTITY getEntity(UUID id, Object... args) {

    Optional<ENTITY> fetchEntityOpt = getRepository().findById(id);

    ENTITY fetchedEntity = fetchEntityOpt.orElseThrow(
        () -> new EntityNotFoundException(
            String.format("%s = %s not found", getEntityClass().getSimpleName(), id),
            getEntityClass(),
            id
        )
    );

    return fetchedEntity;
  }

  protected Page<ENTITY> getEntities(Pageable pageable) {
    return getRepository().findAll(pageable);
  }

  protected Page<ENTITY> getEntitiesBySpec(Specification<ENTITY> specification, Pageable pageable) {
    return getRepository().findAll(specification, pageable);
  }

  protected ENTITY updateEntity(ENTITY entity, REQUEST_VO vo, Object... args) {

    getMapper().updateEntity(entity, vo);

    return getRepository().save(entity);
  }

  protected abstract Class<ENTITY> getEntityClass();
}
