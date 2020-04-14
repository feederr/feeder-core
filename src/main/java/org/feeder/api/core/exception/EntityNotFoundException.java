package org.feeder.api.core.exception;

import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EntityNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 854830213133764487L;

  private final Class<?> entityClass;

  private final UUID entityId;
}
