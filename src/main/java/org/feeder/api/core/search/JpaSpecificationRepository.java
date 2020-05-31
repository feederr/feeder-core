package org.feeder.api.core.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaSpecificationRepository<ENTITY, ID> extends JpaRepository<ENTITY, ID>,
    JpaSpecificationExecutor<ENTITY> {

}
