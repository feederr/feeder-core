package org.feeder.api.core.tenancy;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenancyRequestContextHolder {

  private static final ThreadLocal<TenancyContext> tenancyRequestContext = new InheritableThreadLocal<>() {
    @Override
    protected TenancyContext initialValue() {
      return new TenancyContext(null);
    }
  };

  public static void setTenancyContext(TenancyContext context) {
    tenancyRequestContext.set(context);
  }

  public static TenancyContext getTenancyContext() {
    return tenancyRequestContext.get();
  }

  public static void clearTenancyContext() {
    tenancyRequestContext.remove();
  }

  @Data
  @AllArgsConstructor
  public static class TenancyContext {

    private UUID userId;
  }
}
