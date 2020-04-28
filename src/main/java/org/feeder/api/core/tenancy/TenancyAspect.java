package org.feeder.api.core.tenancy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.framework.Advised;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class TenancyAspect {

  private final TenancyJpaFilter filter;

  // execution of any method with any parameters in Repository and its subclasses with any return type
  @Before("execution(* org.springframework.data.repository.Repository+.*(..))")
  public void applyTenancyJpaFilter(JoinPoint joinPoint) {

    if (shouldApplyFilter(joinPoint)) {
      filter.enable(TenancyRequestContextHolder.getTenancyContext().getUserId());
    } else {
      filter.disable();
    }

  }

  private boolean shouldApplyFilter(JoinPoint joinPoint) {

    Advised target = (Advised) joinPoint.getTarget();

    try {

      TenancyAwareRepository<?, ?> advisedRepository = (TenancyAwareRepository<?, ?>) target
          .getTargetSource().getTarget();

      Class<?> entityClass = advisedRepository.getEntityClass();

      return filter.supports(entityClass);

    } catch (Exception ex) {
      log.error("Failed to obtain {}", TenancyAwareRepository.class.getSimpleName());
      return false;
    }

  }
}
