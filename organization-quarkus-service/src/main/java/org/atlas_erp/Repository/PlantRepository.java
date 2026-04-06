package org.atlas_erp.Repository;

import org.atlas_erp.Entity.PlantEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class PlantRepository implements PanacheRepositoryBase<PlantEntity, UUID> {
}
