package com.atlas_erp.hr_service.Repository;

import org.springframework.stereotype.Repository;
import com.atlas_erp.hr_service.Entity.SkillEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SkillRepository extends JpaRepository<SkillEntity, UUID> {

}
