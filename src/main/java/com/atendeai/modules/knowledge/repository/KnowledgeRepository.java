package com.atendeai.modules.knowledge.repository;

import com.atendeai.modules.knowledge.model.KnowledgeDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KnowledgeRepository extends JpaRepository<KnowledgeDocument, UUID> {
    List<KnowledgeDocument> findByCustomer_IdAndActiveTrueOrderByCreatedAtDesc(UUID customerId);
    List<KnowledgeDocument> findByCustomerIsNullAndActiveTrue();
}
