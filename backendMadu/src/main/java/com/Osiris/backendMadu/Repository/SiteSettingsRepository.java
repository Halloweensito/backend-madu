package com.Osiris.backendMadu.Repository;

import com.Osiris.backendMadu.Entity.SiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface SiteSettingsRepository extends JpaRepository<SiteSettings, Long> {

}
