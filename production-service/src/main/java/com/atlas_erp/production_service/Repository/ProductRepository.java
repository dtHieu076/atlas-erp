package com.atlas_erp.production_service.Repository;

import com.atlas_erp.production_service.Entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    Optional<ProductEntity> findByCode(String code);

    boolean existsByCode(String code);
}
