package org.feeder.api.core.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

@Setter
@Getter
public abstract class BaseEntity<ID> implements Persistable<ID> {

  private boolean isNew = false;
}
