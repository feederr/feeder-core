package org.feeder.api.core.domain;

import javax.persistence.EntityListeners;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<ID> implements Persistable<ID> {

  private transient boolean isNew = false;
}
