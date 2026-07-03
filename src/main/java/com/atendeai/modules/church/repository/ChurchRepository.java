package com.atendeai.modules.church.repository;

import com.atendeai.modules.church.model.Church;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChurchRepository extends JpaRepository<Church, UUID> {

    List<Church> findByActiveTrueOrderByNameAsc();
}
