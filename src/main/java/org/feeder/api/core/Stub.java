package org.feeder.api.core;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Stub {

  private String field;

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }
}
