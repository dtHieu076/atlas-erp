package com.atlas_erp.hr_service.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.atlas_erp.hr_service.Entity.SkillCriteriaEntity;

@Repository
public interface SkillCriteriaRepository extends JpaRepository<SkillCriteriaEntity, UUID> {
    List<SkillCriteriaEntity> findBySkillId(UUID skillId);
}
