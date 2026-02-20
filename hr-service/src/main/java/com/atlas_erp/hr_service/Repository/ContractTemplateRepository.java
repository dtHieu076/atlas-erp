package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.ContractTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContractTemplateRepository
        extends JpaRepository<ContractTemplateEntity, UUID> {

}