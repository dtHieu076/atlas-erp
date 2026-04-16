package org.atlas_erp.Repository;

import org.atlas_erp.Entity.MailCampaign;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MailCampaignRepository implements PanacheRepository<MailCampaign> {
}
