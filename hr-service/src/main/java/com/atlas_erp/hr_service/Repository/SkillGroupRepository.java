package com.atlas_erp.hr_service.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.atlas_erp.hr_service.Entity.SkillGroupEntity;

@Repository
public interface SkillGroupRepository extends JpaRepository<SkillGroupEntity, UUID> {

}
