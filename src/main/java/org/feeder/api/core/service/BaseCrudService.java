package org.feeder.api.core.service;

import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.feeder.api.core.exception.EntityNotFoundException;
import org.feeder.api.core.mapper.BaseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class BaseCrudService<ENTITY, REQUEST_VO, RESPONSE_VO> {

  protected abstract BaseMapper<ENTITY, REQUEST_VO, RESPONSE_VO> getMapper();

  protected abstract JpaRepository<ENTITY, UUID> getRepository();

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
  public Page<RESPONSE_VO> getAll(Pageable pageable, Object... args) {

    log.debug("Get {} on page: {} with size: {}",
        getEntityClass().getSimpleName(), pageable.getPageNumber(), pageable.getPageSize());

    Page<ENTITY> fetchedEntities = getEntities(pageable, args);

    return fetchedEntities.map(
        entity -> getMapper().toResponseVO(entity, args)
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

  protected abstract ENTITY createEntity(REQUEST_VO vo, UUID id, Object... args);

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

  protected Page<ENTITY> getEntities(Pageable pageable, Object... args) {
    return getRepository().findAll(pageable);
  }

  protected abstract ENTITY updateEntity(ENTITY entity, REQUEST_VO vo, Object... args);

  protected abstract Class<ENTITY> getEntityClass();
}
