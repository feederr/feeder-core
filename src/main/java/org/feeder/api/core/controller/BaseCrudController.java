package org.feeder.api.core.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.UUID;
import javax.validation.Valid;
import org.feeder.api.core.domain.BaseEntity;
import org.feeder.api.core.service.BaseCrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class BaseCrudController<ENTITY extends BaseEntity, REQUEST_VO, RESPONSE_VO> {

  protected static final String ID_PATH = "/{id}";

  protected abstract BaseCrudService<ENTITY, REQUEST_VO, RESPONSE_VO> getService();

  @PostMapping
  public ResponseEntity<RESPONSE_VO> create(@Valid @RequestBody final REQUEST_VO vo) {
    UUID id = UUID.randomUUID();
    return ResponseEntity.status(CREATED)
        .contentType(APPLICATION_JSON)
        .body(getService().create(vo, id));
  }

  @GetMapping(ID_PATH)
  public ResponseEntity<RESPONSE_VO> get(@PathVariable final UUID id) {
    return ResponseEntity.status(OK)
        .contentType(APPLICATION_JSON)
        .body(getService().get(id));
  }

  @GetMapping
  public ResponseEntity<Page<RESPONSE_VO>> getPage(@PageableDefault final Pageable pageable) {
    return ResponseEntity.status(OK)
        .contentType(APPLICATION_JSON)
        .body(getService().getAll(pageable));
  }

  @PutMapping(ID_PATH)
  public ResponseEntity<RESPONSE_VO> update(
      @PathVariable final UUID id,
      @Valid @RequestBody final REQUEST_VO vo) {
    return ResponseEntity.status(OK)
        .contentType(APPLICATION_JSON)
        .body(getService().update(vo, id));
  }

  @DeleteMapping(ID_PATH)
  public ResponseEntity<RESPONSE_VO> delete(@PathVariable final UUID id) {
    getService().delete(id);
    return ResponseEntity.status(NO_CONTENT)
        .build();
  }
}
